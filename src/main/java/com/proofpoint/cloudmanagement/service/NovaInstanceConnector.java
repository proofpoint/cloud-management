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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;
import com.proofpoint.cloudmanagement.service.inventoryclient.InventoryClient;
import com.proofpoint.cloudmanagement.service.inventoryclient.InventorySystem;
import com.proofpoint.log.Logger;
import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.domain.Flavor;
import org.jclouds.openstack.nova.domain.Image;
import org.jclouds.openstack.nova.domain.Server;
import org.jclouds.openstack.nova.domain.ServerStatus;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class NovaInstanceConnector implements InstanceConnector
{
    private static final Logger log = Logger.get(NovaInstanceConnector.class);

    private final NovaClient novaClient;
    private static final String NOVA_PROVIDER_NAME = "nova";
    private final String defaultImageRef;
    private final InventoryClient inventoryClient;

    private final Cache<String, Instance> instanceCache;
    private final LoadingCache<String, Flavor> flavorCache;

    @Inject
    public NovaInstanceConnector(NovaConfig config, InventoryClient inventoryClient)
    {
        Properties overrides = new Properties();
        overrides.setProperty(Constants.PROPERTY_ENDPOINT, config.getLocation());

        this.inventoryClient = inventoryClient;

        ComputeServiceContext context = new ComputeServiceContextFactory().createContext(
                NOVA_PROVIDER_NAME,
                config.getUser(),
                config.getApiKey(),
                Collections.<Module>emptySet(), overrides);

        this.novaClient = (NovaClient) context.getProviderSpecificContext().getApi();

        Image defaultImage = novaClient.getImage(config.getDefaultImageId());
        Preconditions.checkNotNull(defaultImage, "No image found for default image id [" + config.getDefaultImageId() + "] please verify that this image exists");

        defaultImageRef = defaultImage.getSelfURI().toString();

        instanceCache = CacheBuilder.newBuilder()
                .expireAfterWrite(7, TimeUnit.DAYS)
                .build();

        flavorCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build(
                        new CacheLoader<String, Flavor>()
                        {

                            @Override
                            public Flavor load(String id)
                                    throws Exception
                            {
                                return novaClient.getFlavor(id);
                            }
                        }
                );
    }

    @PostConstruct
    public void prechargeCaches()
    {
        getSizes();
        getAllInstances();
    }

    public Instance createInstance(final String sizeName, String username)
    {
        Flavor flavor = getFlavorForSizeName(sizeName);
        Preconditions.checkNotNull(flavor, "No size found for name [" + sizeName + "] please verify that this is a valid size.");

        Server server = novaClient.createServer(username + "'s " + sizeName + " instance", defaultImageRef, flavor.getSelfURI().toString());

        try {
            String inventoryName = inventoryClient.getPcmSystemName(server.getUuid());
            InventorySystem inventorySystem = new InventorySystem(inventoryName);
            inventorySystem.setPicInstance(Integer.toString(server.getId()));
        }
        catch (Exception e) {
            log.error("Expected to get a server name from inventory for serverId [" + server.getUuid() + "] but caught exception " + e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return retrieveInstanceByServerId(server.getUuid());
    }

    public Iterable<Instance> getAllInstances()
    {

        Iterable<Server> servers = novaClient.listServers();

        return Iterables.transform(servers, new Function<Server, Instance>()
        {
            @Override
            public Instance apply(@Nullable Server server)
            {
                return retrieveInstanceByServerId(server.getUuid());
            }
        });
    }

    public Instance getInstance(String id)
    {
        return retrieveInstanceByServerId(id);
    }

    public InstanceDestructionStatus destroyInstance(String id)
    {
        if (!instanceExists(id)) {
            return InstanceDestructionStatus.NOT_FOUND;
        }

        novaClient.deleteServer(id);

        return InstanceDestructionStatus.DESTROYED;
    }

    private boolean instanceExists(String id)
    {
        return (instanceCache.getIfPresent(id) != null || novaClient.getServer(id) != null);
    }

    private Instance retrieveInstanceByServerId(String serverId)
    {
        Instance cachedInstance = instanceCache.getIfPresent(serverId);

        if (cachedInstance != null) {
            return cachedInstance;
        }

        Server populatedServer = novaClient.getServer(serverId);

        if (populatedServer == null) {
            return null;
        }

        try {
            String inventoryName = inventoryClient.getPcmSystemName(populatedServer.getUuid());
            InventorySystem inventorySystem = inventoryClient.getSystem(inventoryName);

            Flavor flavor = flavorCache.getUnchecked(String.valueOf(populatedServer.getFlavor().getId()));
            ServerStatus status = populatedServer.getStatus();

            Instance instance = new Instance(populatedServer.getUuid(), populatedServer.getName(), flavor.getName(),
                    status.name(), inventorySystem.getFqdn(), inventorySystem.getTagList());

            if (status == ServerStatus.ACTIVE) {
                instanceCache.put(instance.getId(), instance);
            }
            return instance;
        }
        catch (Exception e) {
            log.error("Expected to find an instance for serverId [" + serverId + "] but caught exception " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Iterable<Size> getSizes()
    {
        ImmutableSet.Builder<Size> sizeSetBuilder = ImmutableSet.<Size>builder();
        for (Flavor flavor : novaClient.listFlavors()) {
            Flavor populatedFlavor = flavorCache.getUnchecked(String.valueOf(flavor.getId()));
            if (!populatedFlavor.getName().contains("deprecated")) {
                sizeSetBuilder.add(Size.fromFlavor(populatedFlavor));
            }
        }
        return sizeSetBuilder.build();
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

    private Flavor getFlavorForSizeName(final String sizeName)
    {
        if (flavorCache.asMap().values().isEmpty()) {
            getSizes();
        }

        return Iterables.find(flavorCache.asMap().values(), new Predicate<Flavor>()
        {
            @Override
            public boolean apply(@Nullable Flavor flavor)
            {
                return Size.fromFlavor(flavor).getName().equals(sizeName);  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }
}
