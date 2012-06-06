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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.proofpoint.units.DataSize;
import com.proofpoint.units.DataSize.Unit;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class InMemoryInstanceConnector implements InstanceConnector
{
    private final ConcurrentMap<String, Instance> instanceMap = new MapMaker().makeMap();
    private static final Set<Size> SIZE_SET = ImmutableSet.of(
            new Size("m1.tiny", 1, new DataSize(512, Unit.MEGABYTE), new DataSize(10, Unit.GIGABYTE)),
            new Size("m1.small", 1, new DataSize(1, Unit.GIGABYTE), new DataSize(100, Unit.GIGABYTE)),
            new Size("c1.small", 1, new DataSize(1, Unit.GIGABYTE), new DataSize(30, Unit.GIGABYTE)),
            new Size("m1.medium", 2, new DataSize(2, Unit.GIGABYTE), new DataSize(200, Unit.GIGABYTE)));

    @Override
    public Iterable<Instance> getAllInstances()
    {
        return instanceMap.values();
    }

    @Override
    public Instance getInstance(String instanceId)
    {
        return instanceMap.get(instanceId);
    }

    @Override
    public InstanceDestructionStatus destroyInstance(String id)
    {
        if (instanceMap.remove(id) != null) {
            return InstanceDestructionStatus.DESTROYED;
        }
        return InstanceDestructionStatus.NOT_FOUND;
    }

    @Override
    public String createInstance(String sizeName, String namePrefix, String locationId)
    {
        String id = UUID.randomUUID().toString();
        Instance instance = new Instance(id, namePrefix + "-" + UUID.randomUUID().toString(), sizeName, "ACTIVE", locationId);
        instanceMap.put(id, instance);
        return id;
    }

    @Override
    public Iterable<Size> getSizes(String location)
    {
        return SIZE_SET;
    }

    @Override
    public String getName()
    {
        return "In Memory Instance Connector";
    }

    @Override
    public Iterable<Location> getLocations()
    {
        return ImmutableList.of(new Location("in-memory", "In your memory"));
    }

    @Override
    public Location getLocation(String location)
    {
        if (location == "in-memory") {
            return new Location("in-memory", "In your memory", SIZE_SET);
        }
        else {
            return null;
        }
    }
}
