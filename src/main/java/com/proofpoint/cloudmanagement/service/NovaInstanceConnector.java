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
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
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

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;

public class NovaInstanceConnector implements InstanceConnector
{
    private static final Logger log = Logger.get(NovaInstanceConnector.class);

    private final NovaClient novaClient;
    private static final String NOVA_PROVIDER_NAME = "nova";
    private final String defaultImageRef;
    private final InventoryClient inventoryClient;

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
    }

    public Instance createInstance(String role, int flavorId)
    {
        Flavor flavor = novaClient.getFlavor(flavorId);
        Preconditions.checkNotNull(flavor, "No flavor found for flavor id [" + flavorId + "] please verify that this is a valid flavor.");

        Server server = novaClient.createServer("PCMCreatedInstance", defaultImageRef, flavor.getSelfURI().toString());

        try {
            String inventoryName = inventoryClient.getPcmSystemName(server.getUuid());

            log.info("Server to send to inventory [" + server + "] with id [" + Integer.toString(server.getId()) + "]");

            InventorySystem inventorySystem = new InventorySystem(inventoryName);
            inventorySystem.setPicInstance(Integer.toString(server.getId()));
            inventorySystem.setRoles(Sets.<String>newTreeSet(Splitter.on(",").split(role)));

            log.info("InvetorySystem sent to inventory [" + inventorySystem + "]" );

            inventoryClient.patchSystem(inventorySystem);
        } catch (Exception e) {
            log.error("Exception caught attempting to talk to inventory :", e);
            throw new RuntimeException(e);
        }

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
        Server server = novaClient.getServer(id);
        Preconditions.checkNotNull(server, "No server found for server id [" + id + "] please verify that this server exists.");

        novaClient.deleteServer(id);
    }

    private Instance convertToInstance(Server server)
    {
        Server populatedServer = novaClient.getServer(server.getId());

        String id = server.getUuid();
        String name = populatedServer.getName();
        String inventoryName = null;
        InventorySystem inventorySystem = null;

        try {
            inventoryName = inventoryClient.getPcmSystemName(id);

            inventorySystem = inventoryClient.getSystem(inventoryName);
        } catch (Exception e) {
            log.error("Exception caught attempting to talk to inventory :", e);
            throw new RuntimeException(e);
        }

        Flavor minimalFlavor = populatedServer.getFlavor();
        String flavorName = null;
        if (minimalFlavor != null) {
            String flavorId = Integer.toString(minimalFlavor.getId());
            Flavor flavor = novaClient.getFlavor(Integer.valueOf(flavorId));
            if (flavor != null) {
                flavorName = flavor.getName();
            }
        }

        String status = null;
        if (populatedServer.getStatus() != null) {
            status = populatedServer.getStatus().value();
        }

        Set<String> roles = inventorySystem.getRoles();

        return new Instance(id, name, flavorName, status, inventorySystem.getFqdn(), Joiner.on(", ").join(roles));
    }
}
