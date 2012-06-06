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
import com.google.common.collect.ImmutableList;

public class Instance
{
    private final String id;
    private final String name;
    private final String status;
    private final String size;
    private final String provider;
    private final String location;
    private final String hostname;
    private final Iterable<String> tags;

    public Instance(String id, String name, String size, String status, String location)
    {
        this(id, name, size, status, null, location, null, null);
    }

    public Instance(String id, String name, String size, String status, String provider, String location, String hostname, Iterable<String> tags)
    {
        Preconditions.checkNotNull(id);
        this.id = id;
        this.name = name;
        this.size = size;
        this.status = status;
        this.provider = provider;
        this.location = location;
        this.hostname = hostname;
        if (tags != null) {
            this.tags = ImmutableList.copyOf(tags);
        }
        else {
            this.tags = null;
        }
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getStatus()
    {
        return status;
    }

    public String getSize()
    {
        return size;
    }

    public String getProvider()
    {
        return provider;
    }

    public String getLocation()
    {
        return location;
    }

    public String getHostname()
    {
        return hostname;
    }

    public Iterable<String> getTags()
    {
        return tags;
    }

    public Builder toBuilder()
    {
        return new Builder().setId(id).setName(name).setStatus(status).setSize(size).setProvider(provider).setLocation(location).setHostname(hostname).setTags(tags);
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

        Instance instance = (Instance) o;

        if (id != null ? !id.equals(instance.id) : instance.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return "Instance{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", size='" + size + '\'' +
                ", provider='" + provider + '\'' +
                ", location='" + location + '\'' +
                ", hostname='" + hostname + '\'' +
                ", tags=" + tags +
                '}';
    }

    public static class Builder
    {
        private String id;
        private String name;
        private String size;
        private String status;
        private String provider;
        private String location;
        private String hostname = null;
        private Iterable<String> tags = null;

        public Builder setId(String id)
        {
            this.id = id;
            return this;
        }

        public Builder setName(String name)
        {
            this.name = name;
            return this;
        }

        public Builder setSize(String size)
        {
            this.size = size;
            return this;
        }

        public Builder setStatus(String status)
        {
            this.status = status;
            return this;
        }

        public Builder setHostname(String hostname)
        {
            this.hostname = hostname;
            return this;
        }

        public Builder setTags(Iterable<String> tags)
        {
            this.tags = tags;
            return this;
        }

        public Builder setProvider(String provider)
        {
            this.provider = provider;
            return this;
        }

        public Builder setLocation(String location)
        {
            this.location = location;
            return this;
        }

        public Instance build()
        {
            return new Instance(id, name, size, status, provider, location, hostname, tags);
        }
    }
}
