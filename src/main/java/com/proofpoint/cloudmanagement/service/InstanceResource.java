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
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Path("/v1/instance/{id: [\\w-]+}")
public class InstanceResource
{
    private final Map<String, InstanceConnector> instanceConnectorMap;
    private TagManager tagManager;
    private DnsManager dnsManager;

    @Inject
    public InstanceResource(Map<String, InstanceConnector> instanceConnectorMap, DnsManager dnsManager, TagManager tagManager)
    {
        checkNotNull(instanceConnectorMap);
        checkNotNull(tagManager);
        checkNotNull(dnsManager);

        this.instanceConnectorMap = instanceConnectorMap;
        this.tagManager = tagManager;
        this.dnsManager = dnsManager;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstance(@PathParam("id") String instanceId, @Context UriInfo uriInfo)
    {
        checkNotNull(instanceId);
        checkNotNull(uriInfo);

        for (Map.Entry<String, InstanceConnector> instanceConnectorEntry : instanceConnectorMap.entrySet()) {
            Instance instance = instanceConnectorEntry.getValue().getInstance(instanceId);
            if (instance != null) {
                return Response.ok(
                        InstanceRepresentation.fromInstance(
                                instance.toBuilder()
                                        .setProvider(instanceConnectorEntry.getKey())
                                        .setHostname(dnsManager.getFullyQualifiedDomainName(instance))
                                        .setTags(tagManager.getTags(instance))
                                        .build(),
                                constructSelfUri(uriInfo, instanceId))).build();
            }
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @DELETE
    public Response deleteInstance(@PathParam("id") String instanceId)
    {
        checkNotNull(instanceId, "Instance ID cannot be null");

        for (InstanceConnector instanceConnector : instanceConnectorMap.values()) {
            if (instanceConnector.destroyInstance(instanceId) == InstanceDestructionStatus.DESTROYED) {
                return Response.noContent().build();
            }
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    public static URI constructSelfUri(UriInfo uriInfo, String id)
    {
        return uriInfo.getBaseUriBuilder().path(InstanceResource.class).build(id);
    }
}
