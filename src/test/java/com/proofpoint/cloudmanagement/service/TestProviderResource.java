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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.proofpoint.jaxrs.testing.MockUriInfo;
import com.proofpoint.testing.Assertions;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import static org.testng.Assert.assertEquals;

public class TestProviderResource
{

    private static final UriInfo MOCK_URI_INFO = MockUriInfo.from("http://localhost/v1/instance");

    private final InMemoryInstanceConnector inMemoryInstanceConnector = new InMemoryInstanceConnector();
    private final String provider = "in-memory";
    private final ImmutableMap<String, InstanceConnector> instanceConnectorMap = ImmutableMap.<String, InstanceConnector>of(provider, inMemoryInstanceConnector);

    @Test
    public void testGetProvider()
    {

        ProviderResource providerResource = new ProviderResource(instanceConnectorMap);

        Iterable<Location> availableLocations = Iterables.transform(inMemoryInstanceConnector.getLocations(), new Function<Location, Location>()
        {
            @Override
            public Location apply(@Nullable Location input)
            {
                return Location.fromLocationAndSelfUri(input, ProviderLocationResource.constructSelfUri(provider, input.getLocation(), MOCK_URI_INFO));  //To change body of implemented methods use File | Settings | File Templates.
            }
        });

        Response response = providerResource.getProvider(provider, MOCK_URI_INFO);

        assertEquals(response.getStatus(), Status.OK.getStatusCode());

        Provider responseEntity = (Provider) response.getEntity();

        assertEquals(responseEntity.getProvider(), provider);
        assertEquals(responseEntity.getName(), inMemoryInstanceConnector.getName());
        assertEquals(responseEntity.getSelf(), ProviderResource.constructSelfUri(MOCK_URI_INFO, provider));
        Assertions.assertEqualsIgnoreOrder(responseEntity.getAvailableLocations(), availableLocations);
    }


    @Test(expectedExceptions = NullPointerException.class)
    public void testNullInstanceMapThrows()
    {
        new ProviderResource(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullProviderGetProviderThrows()
    {
        new ProviderResource(instanceConnectorMap).getProvider(null, MOCK_URI_INFO);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullUriInfoGetProviderThrows()
    {
        new ProviderResource(instanceConnectorMap).getProvider(provider, null);
    }
}
