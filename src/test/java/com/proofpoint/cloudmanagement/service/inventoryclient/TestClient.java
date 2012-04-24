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
package com.proofpoint.cloudmanagement.service.inventoryclient;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.proofpoint.cloudmanagement.service.inventoryclient.MockInventoryServer.MockInventoryServerModule;
import com.proofpoint.configuration.ConfigurationFactory;
import com.proofpoint.configuration.ConfigurationModule;
import com.proofpoint.http.client.UnexpectedResponseException;
import com.proofpoint.http.server.testing.TestingHttpServer;
import com.proofpoint.http.server.testing.TestingHttpServerModule;
import com.proofpoint.jaxrs.JaxrsModule;
import com.proofpoint.json.JsonModule;
import com.proofpoint.node.testing.TestingNodeModule;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.UriBuilder;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TestClient
{
    private InventoryClient client;
    private TestingHttpServer server;
    private MockInventoryServer inventoryServer;

    private static final String INVENTORY_USER = "readonly";
    private static final String INVENTORY_PASSWORD = "readonly";

    @BeforeMethod
    public void setup()
            throws Exception
    {
        Injector inventoryServerInjector = Guice.createInjector(
                new TestingHttpServerModule(),
                new TestingNodeModule(),
                new JaxrsModule(),
                new JsonModule(),
                new MockInventoryServerModule()
        );

        server = inventoryServerInjector.getInstance(TestingHttpServer.class);
        inventoryServer = inventoryServerInjector.getInstance(MockInventoryServer.class);
        server.start();

        Injector injector = Guice.createInjector(
                new JsonModule(),
                new InventoryClientModule(),
                new ConfigurationModule(
                        new ConfigurationFactory(
                                ImmutableMap.<String, String>builder()
                                        .put("inventory.user",
                                                INVENTORY_USER)
                                        .put("inventory.password",
                                                INVENTORY_PASSWORD)
                                        .put("inventory.base-uri",
                                                UriBuilder.fromUri(server.getBaseUrl())
                                                        .path("/inv_api/v1")
                                                        .build()
                                                        .toString())
                                        .build())));

        client = injector.getInstance(InventoryClient.class);
    }


    @Test
    public void testGetSystem()
            throws Exception
    {
        String id = UUID.randomUUID().toString();

        Map<String, String> response = inventoryServer.getPcmName(id);
        String name = response.get("fqdn");

        InventorySystem system = client.getSystem(name);
        Assert.assertEquals(system.getSerialNumber(), id);
    }

    @Test
    public void testGetMissingSystem()
            throws Exception
    {
        InventorySystem system = client.getSystem(UUID.randomUUID().toString());
        Assert.assertNull(system);
    }

    @Test
    public void testSetSystemRoles()
            throws Exception
    {
        String id = UUID.randomUUID().toString();
        Map<String, String> response = inventoryServer.getPcmName(id);
        String name = response.get("fqdn");

        Set<String> roles = ImmutableSet.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        client.patchSystem(new InventorySystem(name).setPicInstance(id).setRoles(roles));

        InventorySystem updated = client.getSystem(name);
        Assert.assertEquals(updated.getRoles(), roles);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Request failed with code 500.*")
    public void testSetSystemRolesForMissingSystem()
            throws Exception
    {
        Set<String> roles = ImmutableSet.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        client.patchSystem(new InventorySystem(UUID.randomUUID().toString()).setPicInstance("").setRoles(roles));
    }

    @Test
    public void testPcmSystemName()
            throws Exception
    {
        String id = UUID.randomUUID().toString();

        String name = client.getPcmSystemName(id);

        // check get same value twice.
        String nameAgain = client.getPcmSystemName(id);
        Assert.assertEquals(name, nameAgain);
    }

    @Test
    public void testAuthorizationEncoding()
    {
        Assert.assertEquals(InventoryClient.basicAuthEncode("user", "pass"), "Basic dXNlcjpwYXNz");
    }
}