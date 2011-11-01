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
package com.proofpoint.cloudmanagement.service.inventoryclient;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Collections;
import java.util.Set;

@JsonSerialize
public class InventorySystem
{
    private String fqdn;
    private String serialNumber;
    private String picInstance;
    private Set<String> roles;

    public InventorySystem(@JsonProperty("fqdn") String fqdn)
    {
        Preconditions.checkNotNull(fqdn, "fqdn may not be null");
        this.fqdn = fqdn;
    }

    @JsonProperty("fqdn")
    public String getFqdn()
    {
        return fqdn;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    @JsonProperty("serial_number")
    public InventorySystem setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
        return this;
    }

    @JsonProperty("pic_instance")
    public String getPicInstance()
    {
        return picInstance;
    }

    @JsonProperty("pic_instance")
    public InventorySystem setPicInstance(String picInstance)
    {
        this.picInstance = picInstance;
        return this;
    }

    @JsonProperty("roles")
    public String getRolesAsSerializedString()
    {
       return Joiner.on(',').join(roles);
    }

    @JsonProperty("roles")
    public InventorySystem setRolesFromSerializedString(String roles)
    {
        if (roles == null) {
            this.roles = Collections.emptySet();
        }
        else {
            this.roles = ImmutableSet.copyOf(Splitter.on(',').trimResults().omitEmptyStrings().split(roles));
        }
        return this;
    }

    public Set<String> getRoles()
    {
        return roles;
    }

    public InventorySystem setRoles(Set<String> roles)
    {
        this.roles = ImmutableSet.copyOf(roles);
        return this;
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

        InventorySystem system = (InventorySystem) o;

        if (fqdn != null ? !fqdn.equals(system.fqdn) : system.fqdn != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return fqdn != null ? fqdn.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return "InventorySystem{" +
                "fqdn='" + fqdn + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", picInstance='" + picInstance + '\'' +
                ", roles=" + roles +
                '}';
    }
}

