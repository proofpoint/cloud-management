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

import com.google.common.collect.ImmutableList;
import com.proofpoint.json.JsonCodec;
import com.proofpoint.testing.EquivalenceTester;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class TestProvider
{

    private final JsonCodec<Provider> providerJsonCodec = JsonCodec.jsonCodec(Provider.class);
    private final JsonCodec<Map<String, Object>> mapJsonCodec = JsonCodec.mapJsonCodec(String.class, Object.class);
    private final JsonCodec<List<Location>> locationListJsonCodec = JsonCodec.listJsonCodec(Location.class);
    private final JsonCodec<List<Object>> listJsonCodec = JsonCodec.listJsonCodec(Object.class);

    @Test
    public void testEquivalence()
            throws URISyntaxException
    {
        List<Location> locationList1 = ImmutableList.of(new Location("a", "aa"));
        List<Location> locationList2 = ImmutableList.of(new Location("b", "bb"));

        EquivalenceTester.equivalenceTester()
                .addEquivalentGroup(new Provider("a", "aa", locationList1, new URI("http://foo")), new Provider("a", "aa", locationList1, new URI("http://foo")))
                .addEquivalentGroup(new Provider("b", "aa", locationList1, new URI("http://foo")), new Provider("b", "aa", locationList1, new URI("http://foo")))
                .addEquivalentGroup(new Provider("a", "bb", locationList1, new URI("http://foo")), new Provider("a", "bb", locationList1, new URI("http://foo")))
                .addEquivalentGroup(new Provider("a", "aa", locationList2, new URI("http://foo")), new Provider("a", "aa", locationList2, new URI("http://foo")))
                .addEquivalentGroup(new Provider("a", "aa", locationList1, new URI("http://bar")), new Provider("a", "aa", locationList1, new URI("http://bar")))
                .check();
    }

    @Test
    public void testJsonMarshalling()
            throws URISyntaxException
    {
        List<Location> locationList = ImmutableList.of(new Location("a", "aa"));
        Provider provider = new Provider("a", "aa", locationList, new URI("http://foo"));
        Map<String, Object> mapEncodedProvider = mapJsonCodec.fromJson(providerJsonCodec.toJson(provider));
        assertEquals(provider.getProvider(), mapEncodedProvider.get("provider"));
        assertEquals(provider.getName(), mapEncodedProvider.get("name"));
        assertEquals(provider.getSelf(), new URI((String) mapEncodedProvider.get("self")));
        assertEquals(listJsonCodec.fromJson(locationListJsonCodec.toJson(ImmutableList.copyOf(provider.getAvailableLocations()))), mapEncodedProvider.get("availableLocations"));
    }

}
