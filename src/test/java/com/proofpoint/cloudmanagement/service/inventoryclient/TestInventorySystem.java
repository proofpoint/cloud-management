package com.proofpoint.cloudmanagement.service.inventoryclient;

import com.google.common.collect.ImmutableSet;
import com.proofpoint.json.JsonCodec;
import org.testng.Assert;
import org.testng.annotations.Test;

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
                        new InventorySystem("name1").setSerialNumber("serial1").setPicInstance("inst1").setRoles(ImmutableSet.of("111")),
                        new InventorySystem("name1").setSerialNumber("serial2").setPicInstance("inst1").setRoles(ImmutableSet.of("111")),
                        new InventorySystem("name1").setSerialNumber("serial1").setPicInstance("inst2").setRoles(ImmutableSet.of("111")),
                        new InventorySystem("name1").setSerialNumber("serial1").setPicInstance("inst1").setRoles(ImmutableSet.of("222")))
                .addEquivalentGroup(
                        new InventorySystem("name2"),
                        new InventorySystem("name2").setSerialNumber("serial1").setPicInstance("inst1").setRoles(ImmutableSet.of("111")),
                        new InventorySystem("name2").setSerialNumber("serial2").setPicInstance("inst1").setRoles(ImmutableSet.of("111")),
                        new InventorySystem("name2").setSerialNumber("serial1").setPicInstance("inst2").setRoles(ImmutableSet.of("111")),
                        new InventorySystem("name2").setSerialNumber("serial1").setPicInstance("inst1").setRoles(ImmutableSet.of("222")))
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
                        .setRoles(ImmutableSet.of("role1", "role2"));

        String encoded = CODEC.toJson(original);
        InventorySystem decoded = CODEC.fromJson(encoded);

        Assert.assertEquals(decoded.getFqdn(), original.getFqdn());
        Assert.assertEquals(decoded.getSerialNumber(), original.getSerialNumber());
        Assert.assertEquals(decoded.getPicInstance(), original.getPicInstance());
        Assert.assertEquals(decoded.getRoles(), original.getRoles());
    }
}
