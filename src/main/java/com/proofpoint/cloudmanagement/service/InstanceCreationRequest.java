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

public class InstanceCreationRequest
{

    private String sizeName;
    private String username;

    @JsonCreator
    public InstanceCreationRequest(@JsonProperty("sizeName") String sizeName, @JsonProperty("username") String username)
    {
        this.sizeName = sizeName;
        this.username = username;
    }

    @JsonProperty
    public String getUsername()
    {
        return username;
    }

    @JsonProperty
    public String getSizeName()
    {
        return sizeName;
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

        if (sizeName != null ? !sizeName.equals(that.sizeName) : that.sizeName != null) {
            return false;
        }
        if (username != null ? !username.equals(that.username) : that.username != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = sizeName != null ? sizeName.hashCode() : 0;
        result = 31 * result + (username != null ? username.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "InstanceCreationRequest{" +
                "sizeName='" + sizeName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
