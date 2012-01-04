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
import com.google.common.collect.ImmutableSet;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/v1/instance")
public class InstancesResource
{

    private final InstanceConnector instanceConnector;

    @Inject
    public InstancesResource(InstanceConnector instanceConnector)
    {
        this.instanceConnector = instanceConnector;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstances(@Context UriInfo uriInfo)
    {
        Preconditions.checkNotNull(uriInfo);

        ImmutableSet.Builder<InstanceRepresentation> representationBuilder = new ImmutableSet.Builder<InstanceRepresentation>();
        for(Instance instance : instanceConnector.getAllInstances())
        {
            representationBuilder.add(InstanceRepresentation.fromInstance(instance, InstanceResource.constructSelfUri(uriInfo, instance.getId())));
        }
        return Response.ok(representationBuilder.build()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createInstance(InstanceCreationRequest request, @Context UriInfo uriInfo)
    {
        Preconditions.checkNotNull(request);
        Preconditions.checkNotNull(uriInfo);

        Instance instance = instanceConnector.createInstance(request.getSizeName(), request.getUsername());
        return Response.created(InstanceResource.constructSelfUri(uriInfo, instance.getId())).build();
    }
}
