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

public class InstanceCreationFailedResponse
{
    private final InstanceCreationRequest requestedInstance;
    private final InstanceCreationError error;

    public InstanceCreationFailedResponse(InstanceCreationRequest requestedInstance, InstanceCreationError error)
    {
        this.error = error;
        this.requestedInstance = requestedInstance;
    }

    @JsonProperty
    public InstanceCreationRequest getRequestedInstance()
    {
        return requestedInstance;
    }

    public InstanceCreationError getError()
    {
        return error;
    }

    @JsonProperty("error")
    public String getErrorMessage()
    {
        return error.getMessage();
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

        InstanceCreationFailedResponse that = (InstanceCreationFailedResponse) o;

        if (error != that.error) {
            return false;
        }
        if (requestedInstance != null ? !requestedInstance.equals(that.requestedInstance) : that.requestedInstance != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = requestedInstance != null ? requestedInstance.hashCode() : 0;
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "InstanceCreationFailedResponse{" +
                "requestedInstance=" + requestedInstance +
                ", error=" + error +
                '}';
    }

    public enum InstanceCreationError
    {
        SIZE_UNAVAILABLE("Size Unavailable"),
        LOCATION_UNAVAILABLE("Location Unavailable"),
        PROVIDER_UNAVAILABLE("Provider Unavailable");

        private String message;

        InstanceCreationError(String message)
        {
            this.message = message;
        }

        public String getMessage()
        {
            return message;
        }
    }
}
