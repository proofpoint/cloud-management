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

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.multibindings.Multibinder;
import com.proofpoint.cloudmanagement.service.DnsManager;
import com.proofpoint.cloudmanagement.service.InstanceCreationNotifier;
import com.proofpoint.cloudmanagement.service.TagManager;
import com.proofpoint.configuration.ConfigurationModule;
import com.proofpoint.http.client.HttpClientBinder;
import com.proofpoint.http.client.HttpClientConfig;

public class InventoryClientModule
        implements Module
{
    public void configure(Binder binder)
    {
        binder.requireExplicitBindings();
        binder.disableCircularProxies();

        ConfigurationModule.bindConfig(binder).to(InventoryClientConfig.class);
        ConfigurationModule.bindConfig(binder).to(HttpClientConfig.class);
        binder.bind(InventoryClient.class).in(Scopes.SINGLETON);

        binder.bind(DnsManager.class).to(InventoryDnsManager.class).in(Scopes.SINGLETON);
        binder.bind(TagManager.class).to(InventoryTagManager.class).in(Scopes.SINGLETON);

        Multibinder<InstanceCreationNotifier> instanceCreationNotifierMultibinder = Multibinder.newSetBinder(binder, InstanceCreationNotifier.class);
        instanceCreationNotifierMultibinder.addBinding().to(InventoryClient.class).in(Scopes.SINGLETON);

        HttpClientBinder.httpClientBinder(binder).bindHttpClient("inventory", Inventory.class);
    }
}
