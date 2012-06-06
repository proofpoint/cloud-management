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

import com.google.common.base.Splitter;
import com.google.inject.Binder;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.proofpoint.configuration.ConfigurationAwareModule;
import com.proofpoint.configuration.ConfigurationFactory;
import com.proofpoint.discovery.client.DiscoveryBinder;

import java.lang.annotation.Annotation;

import static com.proofpoint.configuration.ConfigurationModule.bindConfig;

public class MainModule
        implements ConfigurationAwareModule
{
    public static final String PROVIDER_LISTING_PROPERTY = "cloud-management.providers";
    ConfigurationFactory configurationFactory;

    public void configure(Binder binder)
    {
        binder.requireExplicitBindings();
        binder.disableCircularProxies();

        bindJCloudsInstanceConnectorMap(binder);

        binder.bind(InstancesResource.class).in(Scopes.SINGLETON);
        binder.bind(InstanceResource.class).in(Scopes.SINGLETON);
        binder.bind(TagResource.class).in(Scopes.SINGLETON);

        binder.bind(ProviderResource.class).in(Scopes.SINGLETON);
        binder.bind(ProvidersResource.class).in(Scopes.SINGLETON);
        binder.bind(ProviderLocationResource.class).in(Scopes.SINGLETON);

        DiscoveryBinder.discoveryBinder(binder).bindHttpAnnouncement("cloudmanagement");
    }

    @Override
    public void setConfigurationFactory(ConfigurationFactory configurationFactory)
    {
        this.configurationFactory = configurationFactory;
        configurationFactory.consumeProperty(PROVIDER_LISTING_PROPERTY);
    }

    private void bindJCloudsInstanceConnectorMap(Binder binder)
    {
        MapBinder<String, InstanceConnector> instanceConnectorMapBinder = MapBinder.newMapBinder(binder, String.class, InstanceConnector.class);

        Iterable<String> providersToBind = Splitter.on(",").trimResults().split(getProviderListingProperty());

        for (String provider : providersToBind) {
            Annotation providerNamedAnnotation = Names.named(provider);
            bindConfig(binder).annotatedWith(providerNamedAnnotation).prefixedWith("cloud-management." + provider).to(JCloudsConfig.class);
            instanceConnectorMapBinder.addBinding(provider).toProvider(new InstanceConnectorProvider(providerNamedAnnotation)).in(Scopes.SINGLETON);
        }
    }

    private String getProviderListingProperty()
    {
        return configurationFactory.getProperties().get(PROVIDER_LISTING_PROPERTY);
    }

    private class InstanceConnectorProvider implements Provider<InstanceConnector>
    {
        private final Annotation annotation;
        private Injector injector;

        private InstanceConnectorProvider(Annotation annotation)
        {
            this.annotation = annotation;
        }

        @Inject
        public void setInjector(Injector injector)
        {

            this.injector = injector;
        }

        @Override
        public InstanceConnector get()
        {
            JCloudsConfig config = injector.getInstance(Key.get(JCloudsConfig.class, annotation));
            return new JCloudsInstanceConnector(config);
        }
    }
}
