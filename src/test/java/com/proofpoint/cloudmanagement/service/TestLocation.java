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
import com.proofpoint.units.DataSize;
import com.proofpoint.units.DataSize.Unit;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class TestLocation
{

    JsonCodec<Location> locationJsonCodec = JsonCodec.jsonCodec(Location.class);
    JsonCodec<Map<String, Object>> mapJsonCodec = JsonCodec.mapJsonCodec(String.class, Object.class);
    JsonCodec<List<Object>> listJsonCodec = JsonCodec.listJsonCodec(Object.class);
    JsonCodec<List<Size>> sizeListJsonCodec = JsonCodec.listJsonCodec(Size.class);

    @Test
    public void testEquivalence()
            throws URISyntaxException
    {
        ImmutableList<Size> availableSizes1 = ImmutableList.of(new Size("a", 1, new DataSize(1, Unit.BYTE), new DataSize(1, Unit.KILOBYTE)));
        ImmutableList<Size> availableSizes2 = ImmutableList.of(new Size("b", 2, new DataSize(2, Unit.BYTE), new DataSize(2, Unit.KILOBYTE)));

        EquivalenceTester.equivalenceTester()
                .addEquivalentGroup(new Location("a", "aa", availableSizes1, new URI("http://foo")), new Location("a", "aa", availableSizes1, new URI("http://foo")))
                .addEquivalentGroup(new Location("b", "aa", availableSizes1, new URI("http://foo")), new Location("b", "aa", availableSizes1, new URI("http://foo")))
                .addEquivalentGroup(new Location("a", "bb", availableSizes1, new URI("http://foo")), new Location("a", "bb", availableSizes1, new URI("http://foo")))
                .addEquivalentGroup(new Location("a", "aa", availableSizes2, new URI("http://foo")), new Location("a", "aa", availableSizes2, new URI("http://foo")))
                .addEquivalentGroup(new Location("a", "aa", availableSizes1, new URI("http://bar")), new Location("a", "aa", availableSizes1, new URI("http://bar")))
                .check();
    }

    @Test
    public void testJsonMarshalling()
            throws URISyntaxException
    {
        ImmutableList<Size> availableSizes = ImmutableList.of(new Size("a", 1, new DataSize(1, Unit.BYTE), new DataSize(1, Unit.KILOBYTE)));
        Location testLocation = new Location("a", "aa", availableSizes, new URI("http://foo"));
        Map<String, Object> mapEncodedLocation = mapJsonCodec.fromJson(locationJsonCodec.toJson(testLocation));

        Assert.assertEquals(testLocation.getLocation(), mapEncodedLocation.get("location"));
        Assert.assertEquals(testLocation.getName(), mapEncodedLocation.get("name"));
        Assert.assertEquals(testLocation.getSelf(), new URI((String) mapEncodedLocation.get("self")));
        Assert.assertEquals(listJsonCodec.fromJson(sizeListJsonCodec.toJson(availableSizes)), mapEncodedLocation.get("availableSizes"));
    }
}
