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
import com.google.inject.Binder;
import com.google.inject.Module;
import com.proofpoint.configuration.ConfigurationAwareModule;
import com.proofpoint.configuration.ConfigurationFactory;

public class ConditionalModule implements ConfigurationAwareModule
{
    public static ConfigurationAwareModule installIfPropertyEquals(Module module, String property, String expectedValue)
    {
        return new ConditionalModule(module, property, expectedValue);
    }

    private final Module module;
    private final String property;
    private final String expectedValue;
    private ConfigurationFactory configurationFactory;

    private ConditionalModule(Module module, String property, String expectedValue)
    {
        Preconditions.checkNotNull(module, "module is null");
        Preconditions.checkNotNull(property, "property is null");

        this.module = module;
        this.property = property;
        this.expectedValue = expectedValue;
    }

    @Override
    public void setConfigurationFactory(ConfigurationFactory configurationFactory)
    {
        this.configurationFactory = configurationFactory;
        configurationFactory.consumeProperty(property);

        // consume properties if we are not going to install the module
        if (!shouldInstall()) {
            configurationFactory.registerConfigurationClasses(module);
        }
    }

    @Override
    public void configure(Binder binder)
    {
        Preconditions.checkNotNull(configurationFactory, "configurationFactory is null");
        if (shouldInstall()) {
            binder.install(module);
        }
    }

    private boolean shouldInstall()
    {
        if (expectedValue != null) {
            return expectedValue.equals(configurationFactory.getProperties().get(property));
        }
        else {
            return configurationFactory.getProperties().containsKey(property);
        }
    }
}
