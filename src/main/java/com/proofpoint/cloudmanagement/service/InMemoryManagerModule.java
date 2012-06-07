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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import org.eclipse.jetty.util.ConcurrentHashSet;

import java.util.Set;

public class InMemoryManagerModule
        implements Module
{
    public void configure(Binder binder)
    {
        binder.requireExplicitBindings();
        binder.disableCircularProxies();

        binder.bind(DnsManager.class).to(NoOpDnsManager.class).in(Scopes.SINGLETON);
        binder.bind(TagManager.class).to(InMemoryTagManager.class).in(Scopes.SINGLETON);
    }

    public static class NoOpDnsManager implements DnsManager
    {
        @Override
        public String getFullyQualifiedDomainName(Instance instance)
        {
            return instance.getHostname();
        }
    }

    public static class InMemoryTagManager implements TagManager
    {
        private final LoadingCache<String, Set<String>> tagCache =
                CacheBuilder.newBuilder()
                        .build(new CacheLoader<String, Set<String>>()
                        {

                            @Override
                            public Set<String> load(String key)
                                    throws Exception
                            {
                                return new ConcurrentHashSet<String>();
                            }
                        });

        @Override
        public TagUpdateStatus addTag(Instance instance, String tag)
        {
            tagCache.getUnchecked(instance.getId()).add(tag);
            return TagUpdateStatus.UPDATED;
        }

        @Override
        public TagUpdateStatus deleteTag(Instance instance, String tag)
        {
            tagCache.getUnchecked(instance.getId()).remove(tag);
            return TagUpdateStatus.UPDATED;
        }

        @Override
        public Iterable<String> getTags(Instance instance)
        {
            return tagCache.getUnchecked(instance.getId());
        }
    }
}