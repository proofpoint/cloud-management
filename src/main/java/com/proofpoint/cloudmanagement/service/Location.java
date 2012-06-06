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

public class Location
{

    private final String name;
    private final String location;
    private final URI self;
    private final Iterable<Size> availableSizes;

    public static Location fromLocationAndSelfUri(Location location, URI self)
    {
        return new Location(location.getLocation(), location.getName(), location.getAvailableSizes(), self);
    }

    public Location(String location, String name)
    {
        this(location, name, null, null);
    }

    public Location(String location, String name, URI self)
    {
        this(location, name, null, self);
    }

    public Location(String location, String name, Iterable<Size> availableSizes)
    {
        this(location, name, availableSizes, null);
    }

    public Location(String location, String name, Iterable<Size> availableSizes, URI self)
    {
        this.name = name;
        this.location = location;
        this.self = self;
        this.availableSizes = availableSizes;
    }

    @JsonProperty
    public String getName()
    {
        return name;
    }

    @JsonProperty
    public String getLocation()
    {
        return location;
    }

    @JsonProperty
    public URI getSelf()
    {
        return self;
    }

    @JsonProperty
    public Iterable<Size> getAvailableSizes()
    {
        return availableSizes;
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

        Location that = (Location) o;

        if (availableSizes != null ? !availableSizes.equals(that.availableSizes) : that.availableSizes != null) {
            return false;
        }
        if (location != null ? !location.equals(that.location) : that.location != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
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
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (self != null ? self.hashCode() : 0);
        result = 31 * result + (availableSizes != null ? availableSizes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "Location{" +
                "name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", self=" + self +
                ", availableSizes=" + availableSizes +
                '}';
    }
}
