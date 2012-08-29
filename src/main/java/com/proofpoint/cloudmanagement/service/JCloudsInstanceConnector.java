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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.inject.Module;
import com.proofpoint.log.Logger;
import com.proofpoint.units.DataSize;
import com.proofpoint.units.DataSize.Unit;
import org.jclouds.Constants;
import org.jclouds.aws.ec2.compute.AWSEC2TemplateOptions;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.domain.Volume;
import org.jclouds.domain.Location;
import org.jclouds.openstack.keystone.v2_0.config.CredentialType;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getFirst;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.domain.LocationScope.PROVIDER;
import static org.jclouds.domain.LocationScope.REGION;

public class JCloudsInstanceConnector implements InstanceConnector
{
    private static final Logger log = Logger.get(JCloudsInstanceConnector.class);

    private final ComputeService computeService;
    private final String defaultImageId;

    private final String name;

    private final Map<String, ? extends Hardware> hardwareMap;
    private final Map<String, ? extends Location> locationMap;
    private final String awsVpcSubnetId;

    public JCloudsInstanceConnector(final JCloudsConfig config)
    {
        Preconditions.checkNotNull(config);

        this.name = config.getName();
        this.awsVpcSubnetId = config.getAwsVpcSubnetId();

        Properties overrides = new Properties();
        if (config.getLocation() != null) {
            overrides.setProperty(Constants.PROPERTY_ENDPOINT, config.getLocation());
        }
        overrides.setProperty(KeystoneProperties.CREDENTIAL_TYPE, CredentialType.PASSWORD_CREDENTIALS.toString());
        overrides.setProperty(KeystoneProperties.VERSION, "2.0");

        Set<Module> moduleOverrides = ImmutableSet.<Module>of(new JCloudsLoggingAdapterModule());

        ComputeServiceContext context = new ComputeServiceContextFactory().createContext(
                config.getApi(),
                config.getUser(),
                config.getSecret(),
                moduleOverrides,
                overrides);

        computeService = context.getComputeService();

        //There are too many images in ec2 to list them all, so we can't verify.
        if (!config.getApi().equals("aws-ec2")) {
            Preconditions.checkState(
                    any(computeService.listImages(), new Predicate<org.jclouds.compute.domain.Image>()
                    {
                        @Override
                        public boolean apply(@Nullable org.jclouds.compute.domain.Image image)
                        {
                            return image.getId().endsWith(config.getDefaultImageId());
                        }
                    }), "No image found for default image id [" + config.getDefaultImageId() + "] please verify that this image exists");
        }

        defaultImageId = config.getDefaultImageId();

        hardwareMap = Maps.uniqueIndex(computeService.listHardwareProfiles(), new Function<Hardware, String>()
        {
            @Override
            public String apply(@Nullable Hardware hardware)
            {
                return firstNonNull(hardware.getName(), hardware.getId());
            }
        });

        locationMap = Maps.uniqueIndex(computeService.listAssignableLocations(), new Function<Location, String>()
        {
            @Override
            public String apply(@Nullable Location location)
            {
                return location.getId();
            }
        });

        getAllInstances();
    }

    public String createInstance(final String sizeName, String groupName, String locationId)
    {
        Hardware hardware = hardwareMap.get(sizeName);
        Location location = locationMap.get(locationId);

        Preconditions.checkNotNull(hardware, "No size found for [" + sizeName + "] please verify that this is a valid size.");
        Preconditions.checkNotNull(location, "No location found for [" + locationId + "] please verify that this is a valid location.");

        Set<? extends NodeMetadata> nodes;
        try {
            TemplateBuilder instanceTemplateBuilder = computeService.templateBuilder()
                    .imageId(String.format("%s/%s", locateParentMostRegionOrZone(location).getId(), defaultImageId))
                    .fromHardware(hardware)
                    .locationId(locationId);

            if (awsVpcSubnetId != null) {
                instanceTemplateBuilder.options(AWSEC2TemplateOptions.Builder.subnetId(awsVpcSubnetId).blockUntilRunning(false));
            }
            else {
                instanceTemplateBuilder.options(computeService.templateOptions().blockUntilRunning(false));
            }

            nodes = computeService.createNodesInGroup(groupName, 1, instanceTemplateBuilder.build());
        }
        catch (RunNodesException e) {
            log.error(e, "Couldn't start up instance requested by %s with size %s", groupName, sizeName);
            throw new RuntimeException(e);
        }

        if (nodes == null || nodes.isEmpty()) {
            log.error("Couldn't start up instance requested by %s with size %s", groupName, sizeName);
            throw new RuntimeException("Unknown failure in starting instance.");
        }

        return Iterables.getOnlyElement(nodes).getProviderId();
    }

    private Location locateParentMostRegionOrZone(Location location)
    {
        if (location.getParent() == null || location.getParent().getScope() == PROVIDER) {
            return location;
        }
        return locateParentMostRegionOrZone(location.getParent());
    }

    private Location locateParentMostZone(Location location)
    {
        if (location.getParent() == null || any(of(PROVIDER, REGION), equalTo(location.getParent().getScope()))) {
            return location;
        }
        return locateParentMostZone(location.getParent());
    }

    public Iterable<Instance> getAllInstances()
    {
        return transform(
                filter(computeService.listNodesDetailsMatching(Predicates.<ComputeMetadata>alwaysTrue()),
                        new Predicate<NodeMetadata>()
                        {
                            @Override
                            public boolean apply(@Nullable NodeMetadata input)
                            {
                                return input.getState() != NodeState.TERMINATED;
                            }
                        }), new NodeMetadataToInstance());
    }

    public Instance getInstance(final String id)
    {
        final Set<? extends NodeMetadata> nodeMetadataSet = getNodesWithProviderId(id);

        if (nodeMetadataSet.isEmpty()) {
            return null;
        }

        return new NodeMetadataToInstance().apply(getOnlyElement(nodeMetadataSet));
    }

    private Set<? extends NodeMetadata> getNodesWithProviderId(final String id)
    {
        return computeService.listNodesDetailsMatching(new Predicate<ComputeMetadata>()
            {
                @Override
                public boolean apply(@Nullable ComputeMetadata input)
                {
                    return input.getProviderId().equals(id);
                }
            });
    }

    public InstanceDestructionStatus destroyInstance(String id)
    {
        Set<? extends NodeMetadata> toDestroy = getNodesWithProviderId(id);

        if (toDestroy.isEmpty()) {
            return InstanceDestructionStatus.NOT_FOUND;
        }

        computeService.destroyNode(getOnlyElement(toDestroy).getId());

        return InstanceDestructionStatus.DESTROYED;
    }

    public Iterable<Size> getSizes(final String location)
    {
        return transform(

                Iterables.filter(
                        hardwareMap.entrySet(),
                        new Predicate<Entry<String, ? extends Hardware>>()
                        {
                            @Override
                            public boolean apply(@Nullable Entry<String, ? extends Hardware> input)
                            {
                                return !input.getKey().contains("deprecated") && (input.getValue().getLocation() == null || input.getValue().getLocation().getId().equals(location));
                            }
                        }
                ),

                new Function<Entry<String, ? extends Hardware>, Size>()
                {
                    @Override
                    public Size apply(@Nullable Entry<String, ? extends Hardware> input)
                    {
                        float cpus = 0;
                        for (Processor processor : input.getValue().getProcessors()) {
                            cpus += processor.getCores();
                        }

                        float disk = 0;
                        for (Volume volume : input.getValue().getVolumes()) {
                            disk += volume.getSize();
                        }

                        return new Size(input.getKey(), Math.round(cpus), new DataSize(input.getValue().getRam(), Unit.MEGABYTE), new DataSize(disk, Unit.GIGABYTE));
                    }
                });
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public Iterable<com.proofpoint.cloudmanagement.service.Location> getLocations()
    {
        return transform(computeService.listAssignableLocations(), new Function<Location, com.proofpoint.cloudmanagement.service.Location>()
        {
            @Override
            public com.proofpoint.cloudmanagement.service.Location apply(@Nullable Location input)
            {
                return new com.proofpoint.cloudmanagement.service.Location(input.getId(), input.getDescription());
            }
        });
    }

    @Override
    public com.proofpoint.cloudmanagement.service.Location getLocation(final String location)
    {
        Location jcloudsLocation = Iterables.find(computeService.listAssignableLocations(), new Predicate<Location>()
        {
            @Override
            public boolean apply(@Nullable Location input)
            {
                return input.getId().equals(location);
            }
        });

        return new com.proofpoint.cloudmanagement.service.Location(jcloudsLocation.getId(), jcloudsLocation.getDescription(), this.getSizes(jcloudsLocation.getId()));
    }

    private class NodeMetadataToInstance implements Function<NodeMetadata, Instance>
    {

        @Override
        public Instance apply(@Nullable NodeMetadata input)
        {
            return new Instance.Builder()
                    .setId(input.getProviderId())
                    .setName(input.getName())
                    .setSize(firstNonNull(input.getHardware().getName(), input.getHardware().getId()))
                    .setStatus(input.getState().name())
                    .setLocation(locateParentMostZone(input.getLocation()).getId())
                    .setHostname(getFirst(concat(input.getPublicAddresses(), input.getPrivateAddresses()), input.getHostname()))
                    .build();
        }
    }
}
