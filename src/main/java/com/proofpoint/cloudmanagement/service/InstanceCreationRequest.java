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

import com.google.common.base.Preconditions;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class InstanceCreationRequest
{

    private String name;
    private int flavorId;

    @JsonCreator
    public InstanceCreationRequest(@JsonProperty("name") String name, @JsonProperty("flavorId") Integer flavorId)
    {
        Preconditions.checkNotNull(flavorId, "FlavorId may not be null");
        this.name = name;
        this.flavorId = flavorId;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }

    @JsonProperty
    public int getFlavorId()
    {
        return flavorId;
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

        InstanceCreationRequest that = (InstanceCreationRequest) o;

        if (flavorId != that.flavorId) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + flavorId;
        return result;
    }

    @Override
    public String toString()
    {
        return "InstanceCreationRequest{" +
                "name='" + name + '\'' +
                ", flavorId=" + flavorId +
                '}';
    }
}
