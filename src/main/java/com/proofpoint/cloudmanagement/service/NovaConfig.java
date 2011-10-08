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

import com.proofpoint.configuration.Config;

import javax.validation.constraints.NotNull;

public class NovaConfig
{

    private String location;
    private String user;
    private String apiKey;
    private String defaultImageId;

    @Config("nova.location")
    public NovaConfig setLocation(String location)
    {
        this.location = location;
        return this;
    }

    @Config("nova.user")
    public NovaConfig setUser(String user)
    {
        this.user = user;
        return this;
    }

    @Config("nova.api-key")
    public NovaConfig setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
        return this;
    }

    @Config("nova.default-image-id")
    public NovaConfig setDefaultImageId(String defaultImageId)
    {
        this.defaultImageId = defaultImageId;
        return this;
    }

    @NotNull
    public String getLocation()
    {
        return this.location;
    }

    @NotNull
    public String getUser()
    {
        return this.user;
    }

    @NotNull
    public String getApiKey()
    {
        return this.apiKey;
    }

    @NotNull
    public String getDefaultImageId()
    {
        return defaultImageId;
    }
}
