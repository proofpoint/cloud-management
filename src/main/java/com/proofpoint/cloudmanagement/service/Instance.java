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

import java.util.List;

public class Instance
{

    private final String id;
    private final String name;
    private final String status;
    private final String size;
    private final String image;
    private final List<String> publicIps;
    private final List<String> privateIps;

    @JsonCreator
    public Instance(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("size") String size,
            @JsonProperty("image") String image,
            @JsonProperty("status") String status,
            @JsonProperty("privateIps") List<String> privateIps,
            @JsonProperty("publicIps") List<String> publicIps)
    {
        Preconditions.checkNotNull(id);
        this.id = id;
        this.name = name;
        this.size = size;
        this.status = status;
        this.publicIps = publicIps;
        this.privateIps = privateIps;
        this.image = image;
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
    public List<String> getPublicIps()
    {
        return publicIps;
    }

    @JsonProperty
    public List<String> getPrivateIps()
    {
        return privateIps;
    }

    @JsonProperty
    public String getImage()
    {
        return image;
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
                ", image='" + image + '\'' +
                ", publicIps=" + publicIps +
                ", privateIps=" + privateIps +
                '}';
    }
}
