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
import org.codehaus.jackson.annotate.JsonProperty;

import java.net.URI;

public class InstanceRepresentation
{

    private final String id;
    private final String name;
    private final String status;
    private final String size;
    private final String provider;
    private final String location;
    private final String hostname;
    private final Iterable<String> tags;
    private final URI self;

    public InstanceRepresentation(String id, String name, String size, String status, String provider, String location, String hostname, Iterable<String> tags, URI self)
    {
        this.id = id;
        this.name = name;
        this.size = size;
        this.status = status;
        this.provider = provider;
        this.location = location;
        this.hostname = hostname;
        if (tags == null) {
            this.tags = null;
        }
        else {
            this.tags = ImmutableList.copyOf(tags);
        }
        this.self = self;
    }

    public static InstanceRepresentation fromInstance(Instance instance, URI self)
    {
        return new InstanceRepresentation(instance.getId(), instance.getName(), instance.getSize(), instance.getStatus(), instance.getProvider(), instance.getLocation(), instance.getHostname(), instance.getTags(), self);
    }

    @JsonProperty
    public String getId()
    {
        return id;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }

    @JsonProperty
    public String getStatus()
    {
        return status;
    }

    @JsonProperty
    public String getSize()
    {
        return size;
    }

    @JsonProperty
    public String getProvider()
    {
        return provider;
    }

    @JsonProperty
    public String getLocation()
    {
        return location;
    }

    @JsonProperty
    public String getHostname()
    {
        return hostname;
    }

    @JsonProperty
    public Iterable<String> getTags()
    {
        return tags;
    }

    @JsonProperty
    public URI getSelf()
    {
        return self;
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

        InstanceRepresentation that = (InstanceRepresentation) o;

        if (hostname != null ? !hostname.equals(that.hostname) : that.hostname != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (location != null ? !location.equals(that.location) : that.location != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }
        if (provider != null ? !provider.equals(that.provider) : that.provider != null) {
            return false;
        }
        if (self != null ? !self.equals(that.self) : that.self != null) {
            return false;
        }
        if (size != null ? !size.equals(that.size) : that.size != null) {
            return false;
        }
        if (status != null ? !status.equals(that.status) : that.status != null) {
            return false;
        }
        if (tags != null ? !tags.equals(that.tags) : that.tags != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (provider != null ? provider.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (hostname != null ? hostname.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (self != null ? self.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "InstanceRepresentation{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", size='" + size + '\'' +
                ", provider='" + provider + '\'' +
                ", location='" + location + '\'' +
                ", hostname='" + hostname + '\'' +
                ", tags=" + tags +
                ", self=" + self +
                '}';
    }
}
