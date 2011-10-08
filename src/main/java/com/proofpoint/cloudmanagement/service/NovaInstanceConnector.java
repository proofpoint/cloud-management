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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.Module;
import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.openstack.nova.NovaClient;
import org.jclouds.openstack.nova.domain.Address;
import org.jclouds.openstack.nova.domain.Flavor;
import org.jclouds.openstack.nova.domain.Image;
import org.jclouds.openstack.nova.domain.Server;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class NovaInstanceConnector implements InstanceConnector
{

    private final NovaClient novaClient;
    private static final String NOVA_PROVIDER_NAME = "nova";
    private final String defaultImageRef;

    @Inject
    public NovaInstanceConnector(NovaConfig config)
    {
        Properties overrides = new Properties();
        overrides.setProperty(Constants.PROPERTY_ENDPOINT, config.getLocation());

        ComputeServiceContext context = new ComputeServiceContextFactory().createContext(
                NOVA_PROVIDER_NAME,
                config.getUser(),
                config.getApiKey(),
                Collections.<Module>emptySet(), overrides);

        this.novaClient = (NovaClient) context.getProviderSpecificContext().getApi();

        Image defaultImage = novaClient.getImage(config.getDefaultImageId());
        Preconditions.checkNotNull(defaultImage, "No image found for default image id [" + config.getDefaultImageId() + "] please verify that this image exists");

        defaultImageRef = defaultImage.getSelfURI().toString();
    }

    public Instance createInstance(String name, int flavorId)
    {
        Flavor flavor = novaClient.getFlavor(flavorId);
        Preconditions.checkNotNull(flavor, "No flavor found for flavor id [" + flavorId + "] please verify that this is a valid flavor.");

        Server server = novaClient.createServer(name, defaultImageRef, flavor.getSelfURI().toString());
        return convertToInstance(server);
    }

    public Iterable<Instance> getAllInstances()
    {

        Iterable<Server> servers = novaClient.listServers();

        return Iterables.transform(servers, new Function<Server, Instance>()
        {
            @Override
            public Instance apply(@Nullable Server server)
            {
                return convertToInstance(server);
            }
        });
    }

    public void destroyInstance(String id)
    {
        Server server = novaClient.getServer(Integer.parseInt(id));
        Preconditions.checkNotNull(server, "No server found for server id [" + id + "] please verify that this server exists.");

        novaClient.deleteServer(server.getUuid());
    }

    private Instance convertToInstance(Server server)
    {
        Server populatedServer = novaClient.getServer(server.getId());

        String id = Integer.toString(populatedServer.getId());
        String name = populatedServer.getName();

        String flavorRef = populatedServer.getFlavorRef();
        String flavorName = null;
        if (flavorRef != null && flavorRef.contains("/")) {
            String flavorId = flavorRef.substring(flavorRef.lastIndexOf('/') + 1).trim();
            Flavor flavor = novaClient.getFlavor(Integer.valueOf(flavorId));
            if (flavor != null) {
                flavorName = flavor.getName();
            }
        }

        String imageRef = populatedServer.getImageRef();
        String imageName = null;

        if (imageRef != null && imageRef.contains("/")) {
            String imageId = imageRef.substring(imageRef.lastIndexOf('/') + 1).trim();
            Image image = novaClient.getImage(Integer.valueOf(imageId));
            if (image != null) {
                imageName = image.getName();
            }
        }

        String status = null;
        if (populatedServer.getStatus() != null) {
            status = populatedServer.getStatus().value();
        }
        return new Instance(id, name, flavorName, imageName, status,
                getPrivateAddresses(populatedServer), getPublicAddresses(populatedServer));
    }

    private List<String> getPrivateAddresses(Server populatedServer)
    {
        if (populatedServer.getAddresses() != null) {
            if (populatedServer.getAddresses().getPrivateAddresses() != null) {
                return ImmutableList.copyOf(
                        Iterables.transform(populatedServer.getAddresses().getPrivateAddresses(), new Function<Address, String>()
                        {
                            @Override
                            public String apply(@Nullable Address address)
                            {
                                return address.getAddress();
                            }
                        }));
            }
        }
        return null;
    }

    private List<String> getPublicAddresses(Server populatedServer)
    {
        if (populatedServer.getAddresses() != null) {
            if (populatedServer.getAddresses().getPublicAddresses() != null) {
                return ImmutableList.copyOf(
                        Iterables.transform(populatedServer.getAddresses().getPublicAddresses(), new Function<Address, String>()
                        {
                            @Override
                            public String apply(@Nullable Address address)
                            {
                                return address.getAddress();
                            }
                        }));
            }
        }
        return null;
    }
}
