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

import com.proofpoint.configuration.Config;

import javax.validation.constraints.NotNull;
import java.net.URI;

public class InventoryClientConfig
{
    private URI inventoryUri;
    private String userId;
    private String password;

    @NotNull
    public URI getInventoryUri()
    {
        return inventoryUri;
    }

    @Config("inventory.base-uri")
    public InventoryClientConfig setInventoryUri(String inventoryUri)
    {
        this.inventoryUri = inventoryUri == null ? null : URI.create(inventoryUri);
        return this;
    }

    @NotNull
    public String getUserId()
    {
        return userId;
    }

    @Config("inventory.user")
    public InventoryClientConfig setUserId(String userId)
    {
        this.userId = userId;
        return this;
    }

    @NotNull
    public String getPassword()
    {
        return password;
    }

    @Config("inventory.password")
    public InventoryClientConfig setPassword(String password)
    {
        this.password = password;
        return this;
    }
}

