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
import com.proofpoint.cloudmanagement.service.InstanceConnector.InstanceDestructionStatus;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
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

@Path("/v1/instance/{id: \\w}")
public class InstanceResource
{

    private final InstanceConnector instanceConnector;

    @Inject
    public InstanceResource(InstanceConnector instanceConnector)
    {
        Preconditions.checkNotNull(instanceConnector);

        this.instanceConnector = instanceConnector;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstance(@PathParam("id") String instanceId, @Context UriInfo uriInfo)
    {
        Preconditions.checkNotNull(instanceId);
        Preconditions.checkNotNull(uriInfo);

        Instance instance = instanceConnector.getInstance(instanceId);
        if (instance == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(InstanceRepresentation.fromInstance(instance, uriInfo.getRequestUri())).build();
    }

    @DELETE
    public Response deleteInstance(@PathParam("id") String instanceId)
    {
        Preconditions.checkNotNull(instanceId, "Instance ID cannot be null");

        if (instanceConnector.destroyInstance(instanceId) == InstanceDestructionStatus.NOT_FOUND) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.noContent().build();
    }

    public static URI constructSelfUri(UriInfo uriInfo, String id)
    {
        return uriInfo.getBaseUriBuilder().path(InstanceResource.class).build(id);
    }
}
