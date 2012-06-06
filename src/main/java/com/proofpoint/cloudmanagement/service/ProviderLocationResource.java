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

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
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

@Path("/v1/provider/{provider: [\\w-]+}/location/{location: [\\w-]+}")
public class ProviderLocationResource
{
    private final Map<String, InstanceConnector> instanceConnectorMap;

    @Inject
    public ProviderLocationResource(Map<String, InstanceConnector> instanceConnectorMap)
    {
        checkNotNull(instanceConnectorMap);

        this.instanceConnectorMap = instanceConnectorMap;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLocation(@PathParam("provider") String provider, @PathParam("location") final String location, @Context UriInfo uriInfo)
    {
        checkNotNull(provider);
        checkNotNull(location);
        checkNotNull(uriInfo);

        if (!instanceConnectorMap.containsKey(provider) || !providerHasLocationAvailable(provider, location)) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok(Location.fromLocationAndSelfUri(instanceConnectorMap.get(provider).getLocation(location), constructSelfUri(provider, location, uriInfo))).build();
    }

    private boolean providerHasLocationAvailable(String provider, final String location)
    {
        return Iterables.any(instanceConnectorMap.get(provider).getLocations(), new Predicate<Location>()
        {
            @Override
            public boolean apply(@Nullable Location input)
            {
                return input.getLocation().equals(location);
            }
        });
    }

    public static URI constructSelfUri(String provider, String location, UriInfo uriInfo)
    {
        return uriInfo.getBaseUriBuilder().path(ProviderLocationResource.class).buildFromMap(ImmutableMap.of("provider", provider, "location", location));
    }
}
