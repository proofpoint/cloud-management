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
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/v1/instance/{instance_id: [\\w-]+}/tag/{tag: [\\w-]+}")
public class TagResource
{
    private final InstanceConnector instanceConnector;

    @Inject
    public TagResource(InstanceConnector instanceConnector)
    {
        Preconditions.checkNotNull(instanceConnector);

        this.instanceConnector = instanceConnector;
    }

    @PUT
    public Response addTag(@PathParam("instance_id") String instanceId, @PathParam("tag") String tag)
    {
        Preconditions.checkNotNull(instanceId, "Instance id cannot be null");

        if (instanceConnector.addTag(instanceId, tag) == InstanceConnector.TagUpdateStatus.NOT_FOUND) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.ok().build();
    }

    @DELETE
    public Response deleteTag(@PathParam("instance_id") String instanceId, @PathParam("tag") String tag)
    {
        Preconditions.checkNotNull(instanceId, "Instance id cannot be null");

        if (instanceConnector.deleteTag(instanceId, tag) == InstanceConnector.TagUpdateStatus.NOT_FOUND) {
            return Response.status(Status.NOT_FOUND).build();
        }

        return Response.noContent().build();
    }
}
