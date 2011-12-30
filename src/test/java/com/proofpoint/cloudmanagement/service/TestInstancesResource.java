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
import com.google.common.collect.Iterables;
import com.proofpoint.jaxrs.testing.MockUriInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import java.net.URI;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static com.proofpoint.testing.Assertions.assertEqualsIgnoreOrder;

public class TestInstancesResource
{
    private InstancesResource instancesResource;
    private InMemoryInstanceConnector inMemoryInstanceConnector;

    private static final UriInfo INSTANCES_URI_INFO = MockUriInfo.from("http://localhost/v1/instance");

    @BeforeMethod
    public void setupResource()
    {
        inMemoryInstanceConnector = new InMemoryInstanceConnector();
        instancesResource = new InstancesResource(inMemoryInstanceConnector);
    }

    @Test
    public void testCreateInstaces()
    {
        Response response = instancesResource.createServer(new InstanceCreationRequest("m1.tiny", "mattstep"), INSTANCES_URI_INFO);

        Instance createdInstance = Iterables.getFirst(inMemoryInstanceConnector.getAllInstances(), null);

        assertNotNull(createdInstance);
        assertEquals(response.getStatus(), Status.CREATED.getStatusCode());
        assertEquals(response.getMetadata().getFirst(HttpHeaders.LOCATION), URI.create("http://localhost/v1/instance/" + createdInstance.getId()));
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
        Response createResponse1 = instancesResource.createServer(new InstanceCreationRequest("m1.tiny", "mattstep"), INSTANCES_URI_INFO);
        Response createResponse2 = instancesResource.createServer(new InstanceCreationRequest("m1.small", "mattstep"), INSTANCES_URI_INFO);
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
                        return InstanceRepresentation.fromInstance(instance, InstanceResource.constructSelfUri(INSTANCES_URI_INFO, instance.getId()));
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
        instancesResource.createServer(new InstanceCreationRequest("foo","bar"), null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullRequestThrowsForCreateServer()
    {
        instancesResource.createServer(null, INSTANCES_URI_INFO);
    }
}
