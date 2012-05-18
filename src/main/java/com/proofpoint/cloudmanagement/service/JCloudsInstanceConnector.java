/*
 * Copyright 2010 Proofpoint, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.proofpoint.cloudmanagement.service;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;
import com.proofpoint.cloudmanagement.service.inventoryclient.InventoryClient;
import com.proofpoint.cloudmanagement.service.inventoryclient.InventorySystem;
import com.proofpoint.units.DataSize;
import com.proofpoint.units.DataSize.Unit;
import com.proofpoint.log.Logger;
import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.openstack.keystone.v2_0.config.CredentialType;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class JCloudsInstanceConnector implements InstanceConnector
{
    private static final Logger log = Logger.get(JCloudsInstanceConnector.class);

    private final ComputeService computeService;
    private static final String OPENSTACK_NOVA_PROVIDER_NAME = "openstack-nova";
    private final Image defaultImage;
    private final Location defaultLocation;
    private final LoadingCache<String, InventorySystem> inventoryCache;
    private final InventoryClient inventoryClient;

    private final Cache<String, Instance> instanceCache;
    private final ImmutableMap<String, Hardware> hardwareCache;

    @Inject
    public JCloudsInstanceConnector(final JCloudsConfig config, final InventoryClient inventoryClient)
    {
        this.inventoryClient = inventoryClient;

        Properties overrides = new Properties();
        overrides.setProperty(Constants.PROPERTY_ENDPOINT, config.getLocation());
        overrides.setProperty(KeystoneProperties.CREDENTIAL_TYPE, CredentialType.PASSWORD_CREDENTIALS.toString());
        overrides.setProperty(KeystoneProperties.VERSION, "2.0");

        Set<Module> moduleOverrides = ImmutableSet.<Module>of(new JCloudsLoggingAdapterModule());

        ComputeServiceContext context = new ComputeServiceContextFactory().createContext(
           OPENSTACK_NOVA_PROVIDER_NAME,
                config.getUser(),
                config.getSecret(),
                moduleOverrides,
                overrides);

        computeService = context.getComputeService();

        defaultImage = getImageForName(config.getDefaultImageId());
        Preconditions.checkNotNull(defaultImage, "No image found for default image id [" + config.getDefaultImageId() + "] please verify that this image exists");

        instanceCache = CacheBuilder.newBuilder()
                .expireAfterWrite(7, TimeUnit.DAYS)
                .build();

        ImmutableMap.Builder<String, Hardware> hardwareCacheBuilder = ImmutableMap.builder();
        for(Hardware hardware : computeService.listHardwareProfiles()) {
            hardwareCacheBuilder.put(hardware.getName(), hardware);
        }
        hardwareCache = hardwareCacheBuilder.build();

        defaultLocation = Iterables.find(computeService.listAssignableLocations(), new Predicate<Location>()
        {
            @Override
            public boolean apply(@Nullable Location location)
            {
                return location.getId().equals(config.getDefaultLocationId());
            }
        });

        inventoryCache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<String, InventorySystem>()
                {
                    @Override
                    public InventorySystem load(String id)
                            throws Exception
                    {
                        String inventoryName = inventoryClient.getPcmSystemName(id);
                        return inventoryClient.getSystem(inventoryName);
                    }
                });

        getAllInstances();
    }

    private Image getImageForName(final String defaultImageName)
    {
        return Iterables.find(computeService.listImages(), new Predicate<org.jclouds.compute.domain.Image>()
        {
            @Override
            public boolean apply(@Nullable org.jclouds.compute.domain.Image image)
            {
                return image.getName().equals(defaultImageName);
            }
        });
    }

    public Instance createInstance(final String sizeName, String username)
    {
        Hardware hardware = hardwareCache.get(sizeName);

        Preconditions.checkNotNull(hardware, "No size found for name [" + sizeName + "] please verify that this is a valid size.");

        Set<? extends NodeMetadata> nodes;
        try {
            nodes = computeService.createNodesInGroup(username, 1, new PcmTemplate(hardware));
        }
        catch(RunNodesException e) {
            log.error(e, "Couldn't start up instance requested by %s with size %s", username, sizeName);
            throw new RuntimeException(e);
        }

        if(nodes == null || nodes.isEmpty()) {
            log.error("Couldn't start up instance requested by %s with size %s", username, sizeName);
            throw new RuntimeException("Unknown failure in starting instance.");
        }

        NodeMetadata node = nodes.iterator().next();

        try {
            String inventoryName = inventoryClient.getPcmSystemName(node.getProviderId());
            InventorySystem inventorySystem = new InventorySystem(inventoryName);
            inventorySystem.setPicInstance(node.getId());
            inventoryClient.patchSystem(inventorySystem);
        }
        catch (Exception e) {
            log.error("Expected to get a server name from inventory for serverId [" + node.getId() + "] but caught exception " + e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return retrieveInstanceByNodeId(node.getId());
    }

    public Iterable<Instance> getAllInstances()
    {
        return Iterables.transform(computeService.listNodesDetailsMatching(Predicates.<ComputeMetadata>alwaysTrue()), new NodeMetadataToInstance());
    }

    public Instance getInstance(String id)
    {
        return new NodeMetadataToInstance().apply(computeService.getNodeMetadata(defaultLocation.getId() + "/" + id));
    }

    public InstanceDestructionStatus destroyInstance(String id)
    {
        if (!instanceExists(id)) {
            return InstanceDestructionStatus.NOT_FOUND;
        }

        computeService.destroyNode(id);

        return InstanceDestructionStatus.DESTROYED;
    }

    private boolean instanceExists(String id)
    {
        return (instanceCache.getIfPresent(id) != null || computeService.getNodeMetadata(id) != null);
    }

    private Instance retrieveInstanceByNodeId(String nodeId)
    {
        Instance cachedInstance = instanceCache.getIfPresent(nodeId);

        if (cachedInstance != null) {
            return cachedInstance;
        }

        NodeMetadata populatedNode = computeService.getNodeMetadata(nodeId);

        if (populatedNode == null) {
            return null;
        }

        try {
            String inventoryName = inventoryClient.getPcmSystemName(populatedNode.getId());
            InventorySystem inventorySystem = inventoryClient.getSystem(inventoryName);

            Instance instance = new Instance(populatedNode.getId(), populatedNode.getName(), populatedNode.getHardware().getName(),
                    populatedNode.getState().name(), inventorySystem.getFqdn(), inventorySystem.getTagList());

            if (populatedNode.getState() == NodeState.RUNNING) {
                instanceCache.put(instance.getId(), instance);
            }
            return instance;
        }
        catch (Exception e) {
            log.error("Expected to find an instance for serverId [" + nodeId + "] but caught exception " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Iterable<Size> getSizes()
    {
        return Iterables.transform(
                Iterables.filter(
                        hardwareCache.values(),
                        new Predicate<Hardware>()
                        {
                            @Override
                            public boolean apply(@Nullable Hardware input)
                            {
                                return !input.getName().contains("deprecated");
                            }
                        }
                ),
                new Function<Hardware, Size>() {
                    @Override
                    public Size apply(@Nullable Hardware input)
                    {
                        float cpus = 0;
                        for(Processor processor : input.getProcessors()) {
                            cpus += processor.getCores();
                        }

                        float disk = 0;
                        for(Volume volume : input.getVolumes()) {
                            disk += volume.getSize();
                        }

                        return new Size(input.getName(),
                                        Math.round(cpus),
                                        new DataSize(input.getRam(), Unit.MEGABYTE),
                                        new DataSize(disk, Unit.GIGABYTE));
                    }
                });
    }

    public TagUpdateStatus addTag(String instanceId, String tag)
    {
        Instance instance = getInstance(instanceId);
        if (instance == null) {
            return TagUpdateStatus.NOT_FOUND;
        }
        try {
            InventorySystem inventorySystem = inventoryClient.getSystem(instance.getHostname());
            if (inventorySystem == null) {
                return TagUpdateStatus.NOT_FOUND;
            }
            if (inventorySystem.addTag(tag)) {
                inventoryClient.patchSystem(inventorySystem);
                instanceCache.invalidate(instanceId);
            }
        }
        catch (Exception e) {
            log.error("Exception caught attempting to talk to inventory :", e);
            throw new RuntimeException(e);
        }

        return TagUpdateStatus.UPDATED;
    }

    public TagUpdateStatus deleteTag(String instanceId, String tag)
    {
        Instance instance = getInstance(instanceId);
        if (instance == null) {
            return TagUpdateStatus.NOT_FOUND;
        }
        try {
            InventorySystem inventorySystem = inventoryClient.getSystem(instance.getHostname());
            if (inventorySystem == null) {
                return TagUpdateStatus.NOT_FOUND;
            }
            if (inventorySystem.deleteTag(tag)) {
                inventoryClient.patchSystem(inventorySystem);
                instanceCache.invalidate(instanceId);
            }
        }
        catch (Exception e) {
            log.error("Exception caught attempting to talk to inventory :", e);
            throw new RuntimeException(e);
        }

        return TagUpdateStatus.UPDATED;
    }

    private class NodeMetadataToInstance implements Function<NodeMetadata, Instance>{

        @Override
        public Instance apply(@Nullable NodeMetadata input)
        {
            InventorySystem inventorySystem = inventoryCache.getUnchecked(input.getProviderId());
            return new Instance(input.getProviderId(), input.getName(), input.getHardware().getName(),
                    input.getState().name(), inventorySystem.getFqdn(), inventorySystem.getTagList());
        }
    }

    private class PcmTemplate implements Template
    {
        private final Hardware hardware;

        public PcmTemplate(Hardware hardware)
        {


            this.hardware = hardware;
        }

        @Override
        public Image getImage()
        {
            return defaultImage;
        }

        @Override
        public Hardware getHardware()
        {
            return hardware;
        }

        @Override
        public Location getLocation()
        {
            return defaultLocation;
        }

        @Override
        public TemplateOptions getOptions()
        {
            return new TemplateOptions();
        }
    }
}
