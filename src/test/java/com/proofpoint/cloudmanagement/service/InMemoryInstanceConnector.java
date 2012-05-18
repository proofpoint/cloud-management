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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.proofpoint.units.DataSize;
import com.proofpoint.units.DataSize.Unit;

import java.util.Arrays;
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
    public Instance createInstance(String sizeName, String username)
    {
        String id = UUID.randomUUID().toString();
        Instance instance = new Instance(id, username + "'s " + sizeName + " instance", sizeName, "ACTIVE",
                id + ".foo.com", Arrays.asList("tag1", "tag2"));
        instanceMap.put(id, instance);
        return instance;
    }

    @Override
    public Iterable<Size> getSizes()
    {
        return SIZE_SET;
    }

    @Override
    public TagUpdateStatus addTag(String instanceId, String tag)
    {
        Instance instance = instanceMap.get(instanceId);
        if (instance == null) {
            return TagUpdateStatus.NOT_FOUND;
        }
        return TagUpdateStatus.UPDATED;
    }

    @Override
    public TagUpdateStatus deleteTag(String instanceId, String tag)
    {
        Instance instance = instanceMap.get(instanceId);
        if (instance == null) {
            return TagUpdateStatus.NOT_FOUND;
        }
        return TagUpdateStatus.UPDATED;
    }
}
