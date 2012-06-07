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

import org.codehaus.jackson.annotate.JsonProperty;

import java.net.URI;

public class Provider
{
    private final String provider;
    private final String name;
    private final URI self;
    private final Iterable<Location> availableLocations;

    public Provider(String provider, String name, URI self)
    {
        this(provider, name, null, self);
    }

    public Provider(String provider, String name, Iterable<Location> availableLocations, URI self)
    {
        this.provider = provider;
        this.name = name;
        this.self = self;
        this.availableLocations = availableLocations;
    }

    @JsonProperty
    public String getProvider()
    {
        return provider;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }

    @JsonProperty
    public URI getSelf()
    {
        return self;
    }

    @JsonProperty
    public Iterable<Location> getAvailableLocations()
    {
        return availableLocations;
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

        Provider that = (Provider) o;

        if (availableLocations != null ? !availableLocations.equals(that.availableLocations) : that.availableLocations != null) {
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

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = provider != null ? provider.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (self != null ? self.hashCode() : 0);
        result = 31 * result + (availableLocations != null ? availableLocations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Provider{" +
                "provider='" + provider + '\'' +
                ", name='" + name + '\'' +
                ", self=" + self +
                ", availableLocations=" + availableLocations +
                '}';
    }
}
