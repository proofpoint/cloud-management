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
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.proofpoint.cloudmanagement.service.InMemoryManagerModule.InMemoryTagManager;
import com.proofpoint.cloudmanagement.service.InMemoryManagerModule.NoOpDnsManager;
import com.proofpoint.jaxrs.testing.MockUriInfo;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import static com.proofpoint.cloudmanagement.service.InstanceCreationFailedResponse.InstanceCreationError.LOCATION_UNAVAILABLE;
import static com.proofpoint.cloudmanagement.service.InstanceCreationFailedResponse.InstanceCreationError.PROVIDER_UNAVAILABLE;
import static com.proofpoint.cloudmanagement.service.InstanceCreationFailedResponse.InstanceCreationError.SIZE_UNAVAILABLE;
import static com.proofpoint.testing.Assertions.assertEqualsIgnoreOrder;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class TestInstancesResource
{
    private InstancesResource instancesResource;
    private InMemoryInstanceConnector inMemoryInstanceConnector;

    private static final UriInfo INSTANCES_URI_INFO = MockUriInfo.from("http://localhost/v1/instance");
    private NoOpDnsManager dnsManager;
    private InMemoryTagManager tagManager;

    @BeforeMethod
    public void setupResource()
    {
        inMemoryInstanceConnector = new InMemoryInstanceConnector();
        dnsManager = new NoOpDnsManager();
        tagManager = new InMemoryTagManager();
        instancesResource = new InstancesResource(ImmutableMap.<String, InstanceConnector>of("in-memory-provider", inMemoryInstanceConnector), dnsManager, tagManager);
        Set<InstanceCreationNotifier> instanceCreationNotifierSet = new HashSet();
        instanceCreationNotifierSet.add(new InstanceCreationNotifier()
        {
            @Override
            public void notifyInstanceCreated(String instanceId)
            {
                assertNotNull(instanceId);
                assertNotNull(inMemoryInstanceConnector.getInstance(instanceId));
            }
        });
        instancesResource.setInstanceCreationNotifiers(instanceCreationNotifierSet);
    }

    @Test
    public void testCreateInstances()
    {
        Response response = instancesResource.createInstance(new InstanceCreationRequest("m1.tiny", "mattstep", "in-memory-provider", "in-memory"), INSTANCES_URI_INFO);

        Instance createdInstance = Iterables.getFirst(inMemoryInstanceConnector.getAllInstances(), null);

        assertNotNull(createdInstance);
        assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
        assertEquals(response.getMetadata().getFirst(HttpHeaders.LOCATION), URI.create("http://localhost/v1/instance/" + createdInstance.getId()));
    }


    @Test
    public void testCreateInstanceWithInvalidProvider()
    {
        InstanceCreationRequest request = new InstanceCreationRequest("m1.tiny", "mattstep", "missing-provider", "in-memory");
        Response response = instancesResource.createInstance(request, INSTANCES_URI_INFO);

        Instance createdInstance = Iterables.getFirst(inMemoryInstanceConnector.getAllInstances(), null);

        assertNull(createdInstance);
        assertEquals(response.getStatus(), Status.BAD_REQUEST.getStatusCode());
        assertEquals(response.getEntity(), new InstanceCreationFailedResponse(request, PROVIDER_UNAVAILABLE));
    }

    @Test
    public void testCreateInstanceWithInvalidLocation()
    {
        InstanceCreationRequest request = new InstanceCreationRequest("m1.tiny", "mattstep", "in-memory-provider", "missing-location");
        Response response = instancesResource.createInstance(request, INSTANCES_URI_INFO);

        Instance createdInstance = Iterables.getFirst(inMemoryInstanceConnector.getAllInstances(), null);

        assertNull(createdInstance);
        assertEquals(response.getStatus(), Status.BAD_REQUEST.getStatusCode());
        assertEquals(response.getEntity(), new InstanceCreationFailedResponse(request, LOCATION_UNAVAILABLE));
    }

    @Test
    public void testCreateInstanceWithInvalidSize()
    {
        InstanceCreationRequest request = new InstanceCreationRequest("missing-size", "mattstep", "in-memory-provider", "in-memory");
        Response response = instancesResource.createInstance(request, INSTANCES_URI_INFO);

        Instance createdInstance = Iterables.getFirst(inMemoryInstanceConnector.getAllInstances(), null);

        assertNull(createdInstance);
        assertEquals(response.getStatus(), Status.BAD_REQUEST.getStatusCode());
        assertEquals(response.getEntity(), new InstanceCreationFailedResponse(request, SIZE_UNAVAILABLE));
    }

    @Test
    public void testGetEmptyInstances()
    {
        Response response = instancesResource.getInstances(INSTANCES_URI_INFO);

        assertTrue(Iterables.isEmpty(inMemoryInstanceConnector.getAllInstances()));
        assertEquals(response.getStatus(), Status.OK.getStatusCode());
        assertTrue(Iterables.isEmpty((Iterable<InstanceRepresentation>) response.getEntity()));
    }

    @Test
    public void testCreateAndGetInstances()
    {
        Response createResponse1 = instancesResource.createInstance(new InstanceCreationRequest("m1.tiny", "mattstep", "in-memory-provider", "in-memory"), INSTANCES_URI_INFO);
        Response createResponse2 = instancesResource.createInstance(new InstanceCreationRequest("m1.small", "mattstep", "in-memory-provider", "in-memory"), INSTANCES_URI_INFO);
        Response getResponse = instancesResource.getInstances(INSTANCES_URI_INFO);

        assertEquals(createResponse1.getStatus(), Status.CREATED.getStatusCode());
        assertEquals(createResponse2.getStatus(), Status.CREATED.getStatusCode());
        assertEquals(Iterables.size(inMemoryInstanceConnector.getAllInstances()), 2);
        assertEquals(getResponse.getStatus(), Status.OK.getStatusCode());
        assertEqualsIgnoreOrder(
                (Iterable<InstanceRepresentation>) getResponse.getEntity(),
                Iterables.transform(inMemoryInstanceConnector.getAllInstances(), new Function<Instance, InstanceRepresentation>()
                {
                    @Override
                    public InstanceRepresentation apply(@Nullable Instance instance)
                    {
                        return InstanceRepresentation.fromInstance(
                                instance.toBuilder()
                                        .setProvider("in-memory-provider")
                                        .setHostname(dnsManager.getFullyQualifiedDomainName(instance))
                                        .setTags(tagManager.getTags(instance))
                                        .build(),
                                InstanceResource.constructSelfUri(INSTANCES_URI_INFO, instance.getId()));
                    }
                }));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullUriInfoThrowsForGetInstances()
    {
        instancesResource.getInstances(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullUriInfoThrowsForCreateServer()
    {
        instancesResource.createInstance(new InstanceCreationRequest("foo", "bar", "in-memory-provider", "in-memory"), null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullRequestThrowsForCreateServer()
    {
        instancesResource.createInstance(null, INSTANCES_URI_INFO);
    }
}
