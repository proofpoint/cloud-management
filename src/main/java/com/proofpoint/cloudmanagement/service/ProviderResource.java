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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Path("/v1/provider/{provider: [\\w-]+}")
public class ProviderResource
{
    private final Map<String, InstanceConnector> instanceConnectorMap;

    @Inject
    public ProviderResource(Map<String, InstanceConnector> instanceConnectorMap)
    {
        checkNotNull(instanceConnectorMap);

        this.instanceConnectorMap = instanceConnectorMap;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProvider(@PathParam("provider") final String provider, @Context final UriInfo uriInfo)
    {
        checkNotNull(provider);
        checkNotNull(uriInfo);

        if (!instanceConnectorMap.containsKey(provider)) {
            return Response.status(Status.NOT_FOUND).build();
        }

        InstanceConnector instanceConnector = instanceConnectorMap.get(provider);

        Provider providerRepresentation = new Provider(
                provider,
                instanceConnector.getName(),
                Iterables.transform(instanceConnector.getLocations(), new Function<Location, Location>()
                {
                    @Override
                    public Location apply(@Nullable Location input)
                    {
                        return Location.fromLocationAndSelfUri(input, ProviderLocationResource.constructSelfUri(provider, input.getLocation(), uriInfo));
                    }
                }),
                constructSelfUri(uriInfo, provider));

        return Response.ok(providerRepresentation).build();
    }

    public static URI constructSelfUri(@Context UriInfo uriInfo, String provider)
    {
        return uriInfo.getBaseUriBuilder().path(ProviderResource.class).build(provider);
    }

}
