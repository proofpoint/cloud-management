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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.proofpoint.cloudmanagement.service.InMemoryManagerModule.InMemoryTagManager;
import com.proofpoint.cloudmanagement.service.InMemoryManagerModule.NoOpDnsManager;
import com.proofpoint.jaxrs.testing.MockUriInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class TestInstanceResource
{
    private InstanceResource instanceResource;
    private InMemoryInstanceConnector inMemoryInstanceConnector;

    private static final UriInfo INSTANCE_URI_INFO = MockUriInfo.from("http://localhost/v1/instance");
    private InMemoryTagManager tagManager;
    private NoOpDnsManager dnsManager;

    @BeforeMethod
    public void setupResource()
    {
        inMemoryInstanceConnector = new InMemoryInstanceConnector();
        tagManager = new InMemoryTagManager();
        dnsManager = new NoOpDnsManager();
        instanceResource = new InstanceResource(ImmutableMap.<String, InstanceConnector>of("in-memory-provider", inMemoryInstanceConnector), dnsManager, tagManager);
    }

    @Test
    public void testGetInstance()
    {
        String createdInstance = inMemoryInstanceConnector.createInstance("m1.tiny", "mattstep", "in-memory");
        Instance instance = inMemoryInstanceConnector.getInstance(createdInstance);
        InstanceRepresentation createdInstanceRepresentation =
                InstanceRepresentation.fromInstance(
                        instance.toBuilder()
                                .setProvider("in-memory-provider")
                                .setTags(tagManager.getTags(instance))
                                .setHostname(dnsManager.getFullyQualifiedDomainName(instance))
                                .build(),
                        InstanceResource.constructSelfUri(INSTANCE_URI_INFO, createdInstance));

        Response response = instanceResource.getInstance(createdInstance, INSTANCE_URI_INFO);

        assertEquals(response.getStatus(), Status.OK.getStatusCode());
        assertEquals(response.getEntity(), createdInstanceRepresentation);
    }

    @Test
    public void testMissingGetInstance()
    {
        Response response = instanceResource.getInstance(UUID.randomUUID().toString(), INSTANCE_URI_INFO);

        assertEquals(response.getStatus(), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testDeleteInstance()
    {
        String createdInstance = inMemoryInstanceConnector.createInstance("m1.tiny", "mattstep", "in-memory");

        Response getResponse1 = instanceResource.getInstance(createdInstance, INSTANCE_URI_INFO);

        assertEquals(getResponse1.getStatus(), Status.OK.getStatusCode());

        Response deleteResponse = instanceResource.deleteInstance(createdInstance);

        assertEquals(deleteResponse.getStatus(), Status.NO_CONTENT.getStatusCode());

        Response getResponse2 = instanceResource.getInstance(createdInstance, INSTANCE_URI_INFO);

        assertEquals(getResponse2.getStatus(), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testDeleteMissingInstance()
    {
        Response deleteResponse = instanceResource.deleteInstance(UUID.randomUUID().toString());

        assertEquals(deleteResponse.getStatus(), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testConstructSelfUri()
    {
        String id = UUID.randomUUID().toString();
        URI self = InstanceResource.constructSelfUri(INSTANCE_URI_INFO, id);

        assertEquals(id, Strings.commonSuffix(id, self.getPath()));
        assertEquals(INSTANCE_URI_INFO.getAbsolutePath().toString(), Strings.commonPrefix(INSTANCE_URI_INFO.getAbsolutePath().toString(), self.toString()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testDeleteWithNullIdThrows()
    {
        instanceResource.deleteInstance(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetWithNullIdThrows()
    {
        instanceResource.getInstance(null, INSTANCE_URI_INFO);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetWithNullUriInfoThrows()
    {
        instanceResource.getInstance(UUID.randomUUID().toString(), null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructionWithNullInstanceConnectorThrows()
    {
        new InstanceResource(null, new NoOpDnsManager(), new InMemoryTagManager());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructionWithNullDnsManagerThrows()
    {
        new InstanceResource(ImmutableMap.<String, InstanceConnector>of("tmp", inMemoryInstanceConnector), null, new InMemoryTagManager());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructionWithNullTagManagerThrows()
    {
        new InstanceResource(ImmutableMap.<String, InstanceConnector>of("tmp", inMemoryInstanceConnector), new NoOpDnsManager(), null);
    }
}
