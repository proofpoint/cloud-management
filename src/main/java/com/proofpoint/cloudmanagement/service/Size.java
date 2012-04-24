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

import com.google.common.base.Objects;
import com.proofpoint.experimental.units.DataSize;
import com.proofpoint.experimental.units.DataSize.Unit;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jclouds.openstack.nova.domain.Flavor;

public class Size
{
    private final String name;
    private final int cores;
    private final DataSize memory;
    private final DataSize disk;

    public Size(String name, int cores, DataSize memory, DataSize disk)
    {
        this.name = name;
        this.cores = cores;
        this.memory = memory;
        this.disk = disk;
    }

    public static Size fromFlavor(Flavor flavor)
    {
        return new Size(flavor.getName(),
                Objects.firstNonNull(flavor.getVcpus(), 0),
                new DataSize(Objects.firstNonNull(flavor.getRam(), 0), Unit.MEGABYTE),
                new DataSize(Objects.firstNonNull(flavor.getDisk(), 0), Unit.GIGABYTE));
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }

    @JsonProperty
    public int getCores()
    {
        return cores;
    }

    @JsonProperty
    public String getMemory()
    {
        if (memory == null) {
            return null;
        }
        return memory.convertToMostSuccinctDataSize().toString();
    }

    @JsonProperty
    public String getDisk()
    {
        if (disk == null) {
            return null;
        }
        return disk.convertToMostSuccinctDataSize().toString();
    }

    @JsonProperty
    public double getCost()
    {
        return cores + Objects.firstNonNull(memory.convertTo(Unit.GIGABYTE).getValue(), 0.0) + Objects.firstNonNull(disk.convertTo(Unit.TERABYTE).getValue(), 0.0);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Size size = (Size) o;

        if (cores != size.cores) {
            return false;
        }
        if (disk != null ? !disk.equals(size.disk) : size.disk != null) {
            return false;
        }
        if (name != null ? !name.equals(size.name) : size.name != null) {
            return false;
        }
        if (memory != null ? !memory.equals(size.memory) : size.memory != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + cores;
        result = 31 * result + (memory != null ? memory.hashCode() : 0);
        result = 31 * result + (disk != null ? disk.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Size{" +
                "name='" + name + '\'' +
                ", cores=" + cores +
                ", memory=" + memory +
                ", disk=" + disk +
                '}';
    }
}
