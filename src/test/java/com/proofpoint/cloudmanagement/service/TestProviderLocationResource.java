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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.proofpoint.jaxrs.testing.MockUriInfo;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

public class TestProviderLocationResource
{
    private static final UriInfo MOCK_URI_INFO = MockUriInfo.from("http://localhost/v1/instance");

    private final InMemoryInstanceConnector inMemoryInstanceConnector = new InMemoryInstanceConnector();
    private final String provider = "in-memory";
    private final ImmutableMap<String, InstanceConnector> instanceConnectorMap = ImmutableMap.<String, InstanceConnector>of(provider, inMemoryInstanceConnector);

    @Test
    public void testGetLocation()
    {
        ProviderLocationResource providerLocationResource = new ProviderLocationResource(instanceConnectorMap);

        Location location = Iterables.getLast(inMemoryInstanceConnector.getLocations());

        Response response = providerLocationResource.getLocation(provider, location.getLocation(), MOCK_URI_INFO);

        Assert.assertEquals(response.getStatus(), Status.OK.getStatusCode());
        Assert.assertEquals(response.getEntity(),
                Location.fromLocationAndSelfUri(inMemoryInstanceConnector.getLocation(location.getLocation()), ProviderLocationResource.constructSelfUri(provider, location.getLocation(), MOCK_URI_INFO)));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullInstanceMapThrows()
    {
        new ProviderLocationResource(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullProviderGetLocationThrows()
    {
        new ProviderLocationResource(instanceConnectorMap).getLocation(null, "foo", MOCK_URI_INFO);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullLocationGetLocationThrows()
    {
        new ProviderLocationResource(instanceConnectorMap).getLocation(provider, null, MOCK_URI_INFO);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullUriInfoGetLocationThrows()
    {
        new ProviderLocationResource(instanceConnectorMap).getLocation(provider, "foo", null);
    }
}
