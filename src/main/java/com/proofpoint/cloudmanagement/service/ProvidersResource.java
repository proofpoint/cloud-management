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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.Map.Entry;

import static com.google.common.base.Preconditions.checkNotNull;

@Path("/v1/provider")
public class ProvidersResource
{
    private final Map<String, InstanceConnector> instanceConnectorMap;

    @Inject
    public ProvidersResource(Map<String, InstanceConnector> instanceConnectorMap)
    {
        checkNotNull(instanceConnectorMap);

        this.instanceConnectorMap = instanceConnectorMap;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProviders(@Context final UriInfo uriInfo)
    {
        checkNotNull(uriInfo);

        return Response.ok(Iterables.transform(instanceConnectorMap.entrySet(), new Function<Entry<String, InstanceConnector>, Object>()
        {
            @Override
            public Object apply(@Nullable Entry<String, InstanceConnector> input)
            {
                return new Provider(input.getKey(), input.getValue().getName(), ProviderResource.constructSelfUri(uriInfo, input.getKey()));
            }
        })).build();
    }
}
