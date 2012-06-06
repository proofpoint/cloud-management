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
import java.util.Collections;
import java.util.Map.Entry;

import static org.testng.Assert.assertEquals;


public class TestProvidersResource
{

    public static final UriInfo MOCK_URI_INFO = MockUriInfo.from("http://localhost/v1/instance");

    @Test
    public void testGetProviders()
    {
        ImmutableMap<String, InstanceConnector> instanceConnectorMap = ImmutableMap.<String, InstanceConnector>of("in-memory", new InMemoryInstanceConnector());
        ProvidersResource providersResource = new ProvidersResource(instanceConnectorMap);

        Response response = providersResource.getProviders(MOCK_URI_INFO);

        assertEquals(response.getStatus(), Status.OK.getStatusCode());

        Assertions.assertEqualsIgnoreOrder((Iterable<Provider>) response.getEntity(), Iterables.transform(instanceConnectorMap.entrySet(), new Function<Entry<String, InstanceConnector>, Provider>()
        {
            @Override
            public Provider apply(@Nullable Entry<String, InstanceConnector> input)
            {
                return new Provider(input.getKey(), input.getValue().getName(), ProviderResource.constructSelfUri(MOCK_URI_INFO, input.getKey()));
            }
        }));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullMapThrows()
    {
        new ProvidersResource(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullUriInfoThrows()
    {
        new ProvidersResource(Collections.<String, InstanceConnector>emptyMap()).getProviders(null);
    }
}
