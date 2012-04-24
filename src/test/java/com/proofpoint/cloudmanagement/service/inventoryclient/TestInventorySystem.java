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

import com.google.common.collect.ImmutableSet;
import com.proofpoint.json.JsonCodec;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static com.proofpoint.testing.EquivalenceTester.equivalenceTester;

public class TestInventorySystem
{
    private static final JsonCodec<InventorySystem> CODEC = JsonCodec.jsonCodec(InventorySystem.class);

    @Test
    public void testEquivalence()
    {
        equivalenceTester()
                .addEquivalentGroup(
                        new InventorySystem("name1"),
                        new InventorySystem("name1").setSerialNumber("serial1").setPicInstance("inst1").setRoles(ImmutableSet.of("111")).setTags(Arrays.asList("tag1")),
                        new InventorySystem("name1").setSerialNumber("serial2").setPicInstance("inst1").setRoles(ImmutableSet.of("111")).setTags(Arrays.asList("tag2")),
                        new InventorySystem("name1").setSerialNumber("serial1").setPicInstance("inst2").setRoles(ImmutableSet.of("111")).setTags(Arrays.asList("tag1")),
                        new InventorySystem("name1").setSerialNumber("serial1").setPicInstance("inst1").setRoles(ImmutableSet.of("222")).setTags(Arrays.asList("tag2")))
                .addEquivalentGroup(
                        new InventorySystem("name2"),
                        new InventorySystem("name2").setSerialNumber("serial1").setPicInstance("inst1").setRoles(ImmutableSet.of("111")).setTags(Arrays.asList("tag1")),
                        new InventorySystem("name2").setSerialNumber("serial2").setPicInstance("inst1").setRoles(ImmutableSet.of("111")).setTags(Arrays.asList("tag2")),
                        new InventorySystem("name2").setSerialNumber("serial1").setPicInstance("inst2").setRoles(ImmutableSet.of("111")).setTags(Arrays.asList("tag1")),
                        new InventorySystem("name2").setSerialNumber("serial1").setPicInstance("inst1").setRoles(ImmutableSet.of("222")).setTags(Arrays.asList("tag2")))
                .check();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullNameThrows()
    {
        new InventorySystem(null);
    }

    @Test
    public void testJsonRoundTrip()
    {
        InventorySystem original =
                new InventorySystem("test")
                        .setSerialNumber("serial")
                        .setPicInstance("instance")
                        .setRoles(ImmutableSet.of("role1", "role2"))
                        .setTags(Arrays.asList("tag1", "tag2"));

        String encoded = CODEC.toJson(original);
        InventorySystem decoded = CODEC.fromJson(encoded);

        Assert.assertEquals(decoded.getFqdn(), original.getFqdn());
        Assert.assertEquals(decoded.getPicInstance(), original.getPicInstance());
        Assert.assertEquals(decoded.getRoles(), original.getRoles());
        Assert.assertEquals(decoded.getTagList(), original.getTagList());
        Assert.assertEquals(decoded.getTags(), original.getTags());

        original.setTags(new ArrayList<String>());
        encoded = CODEC.toJson(original);
        decoded = CODEC.fromJson(encoded);

        Assert.assertEquals(decoded.getTagList(), original.getTagList());
        Assert.assertEquals(decoded.getTags(), original.getTags());
    }
}
