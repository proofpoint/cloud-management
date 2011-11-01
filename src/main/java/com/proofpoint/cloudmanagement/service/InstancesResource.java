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

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v1/instance")
public class InstancesResource
{

    private final InstanceConnector instanceConnector;

    @Inject
    public InstancesResource(NovaInstanceConnector instanceConnector)
    {
        this.instanceConnector = instanceConnector;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstances()
    {
        return Response.ok(instanceConnector.getAllInstances()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createServer(InstanceCreationRequest request)
    {
        return Response.ok(instanceConnector.createInstance(request.getRole(), request.getFlavorId())).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteServer(@PathParam("id") String serverId)
    {

        Preconditions.checkNotNull(serverId, "Server ID cannot be null");

        instanceConnector.destroyInstance(serverId);

        return Response.noContent().build();
    }
}
