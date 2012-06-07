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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.validation.constraints.NotNull;

public class InstanceCreationRequest
{
    private final String size;
    private final String namePrefix;
    private final String provider;
    private final String location;

    @JsonCreator
    public InstanceCreationRequest(@JsonProperty("size") String size, @JsonProperty("namePrefix") String namePrefix, @JsonProperty("provider") String provider, @JsonProperty("location") String location)
    {
        this.size = size;
        this.namePrefix = namePrefix;
        this.provider = provider;
        this.location = location;
    }

    @JsonProperty
    @NotNull
    public String getNamePrefix()
    {
        return namePrefix;
    }

    @JsonProperty
    @NotNull
    public String getSize()
    {
        return size;
    }

    @JsonProperty
    @NotNull
    public String getProvider()
    {
        return provider;
    }

    @JsonProperty
    @NotNull
    public String getLocation()
    {
        return location;
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

        if (location != null ? !location.equals(that.location) : that.location != null) {
            return false;
        }
        if (namePrefix != null ? !namePrefix.equals(that.namePrefix) : that.namePrefix != null) {
            return false;
        }
        if (provider != null ? !provider.equals(that.provider) : that.provider != null) {
            return false;
        }
        if (size != null ? !size.equals(that.size) : that.size != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = size != null ? size.hashCode() : 0;
        result = 31 * result + (namePrefix != null ? namePrefix.hashCode() : 0);
        result = 31 * result + (provider != null ? provider.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "InstanceCreationRequest{" +
                "size='" + size + '\'' +
                ", namePrefix='" + namePrefix + '\'' +
                ", provider='" + provider + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
