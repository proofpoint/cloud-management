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
import com.proofpoint.configuration.ConfigurationFactory;
import com.proofpoint.configuration.ConfigurationModule;
import com.proofpoint.json.JsonModule;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Set;
import java.util.UUID;

public class TestClient
{
    private InventoryClient client;

    private static final String SYSTEM_NAME = "name";
    private static final String OPENSTACK_ID = "id";
    private static final String INVENTORY_BASE_URI = "http://localhost/api";
    private static final String INVENTORY_USER = "readonly";
    private static final String INVENTORY_PASSWORD = "readonly";

    @BeforeMethod
    public void setup()
            throws Exception
    {
        Injector injector = Guice.createInjector(
                new JsonModule(),
                new InventoryClientModule(),
                new ConfigurationModule(
                        new ConfigurationFactory(
                                ImmutableMap.<String, String>builder()
                                        .put("inventory.user", INVENTORY_USER)
                                        .put("inventory.password", INVENTORY_PASSWORD)
                                        .put("inventory.base-uri", INVENTORY_BASE_URI)
                                        .build())));

        client = injector.getInstance(InventoryClient.class);
    }


    @Test(enabled = false)
    public void testGetSystem()
            throws Exception
    {
        InventorySystem system = client.getSystem(SYSTEM_NAME);
        Assert.assertEquals(system.getSerialNumber(), OPENSTACK_ID);
    }

    @Test(enabled = false)
    public void testGetMissingSystem()
            throws Exception
    {
        InventorySystem system = client.getSystem(UUID.randomUUID().toString());
        Assert.assertNull(system);
    }

    @Test(enabled = false)
    public void testSetSystemRoles()
            throws Exception
    {
        Set<String> roles = ImmutableSet.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        client.setSystemRoles(SYSTEM_NAME, roles);

        InventorySystem updated = client.getSystem(SYSTEM_NAME);
        Assert.assertEquals(updated.getRoles(), roles);
    }

    @Test(enabled = false)
    public void testSetSystemRolesForMissingSystem()
            throws Exception
    {
        Set<String> roles = ImmutableSet.of(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        try {
            client.setSystemRoles(UUID.randomUUID().toString(), roles);
        }
        catch (RuntimeException ex) {
            Assert.assertTrue(ex.getMessage().contains("Request failed with code 500"));
            return;
        }
        Assert.fail("Expected an exception to be thrown");
    }

    @Test(enabled = false)
    public void testPcmSystemName()
            throws Exception
    {
        String name = client.getPcmSystemName(OPENSTACK_ID);
        Assert.assertEquals(name, SYSTEM_NAME);

        // check get same value twice.
        name = client.getPcmSystemName(OPENSTACK_ID);
        Assert.assertEquals(name, SYSTEM_NAME);
    }

    @Test
    public void testAuthorizationEncoding()
    {
        Assert.assertEquals(InventoryClient.basicAuthEncode("user", "pass"), "Basic dXNlcjpwYXNz");
    }
}