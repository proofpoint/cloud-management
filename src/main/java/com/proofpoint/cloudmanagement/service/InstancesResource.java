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
import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.proofpoint.cloudmanagement.service.InstanceCreationFailedResponse.InstanceCreationError.LOCATION_UNAVAILABLE;
import static com.proofpoint.cloudmanagement.service.InstanceCreationFailedResponse.InstanceCreationError.PROVIDER_UNAVAILABLE;
import static com.proofpoint.cloudmanagement.service.InstanceCreationFailedResponse.InstanceCreationError.SIZE_UNAVAILABLE;

@Path("/v1/instance")
public class InstancesResource
{
    private final Map<String, InstanceConnector> instanceConnectorMap;
    private final ConcurrentHashMultiset<InstanceCreationNotifier> instanceCreationNotifiers;
    private final TagManager tagManager;
    private final DnsManager dnsManager;

    @Inject
    public InstancesResource(Map<String, InstanceConnector> instanceConnectorMap, DnsManager dnsManager, TagManager tagManager)
    {
        this.instanceConnectorMap = instanceConnectorMap;
        this.instanceCreationNotifiers = ConcurrentHashMultiset.<InstanceCreationNotifier>create();
        this.tagManager = tagManager;
        this.dnsManager = dnsManager;
    }

    @Inject(optional = true)
    public void setInstanceCreationNotifiers(Set<InstanceCreationNotifier> instanceCreationNotifiers)
    {
        this.instanceCreationNotifiers.addAll(instanceCreationNotifiers);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getInstances(@Context UriInfo uriInfo)
    {
        checkNotNull(uriInfo);

        ImmutableSet.Builder<InstanceRepresentation> representationBuilder = new ImmutableSet.Builder<InstanceRepresentation>();

        for (Map.Entry<String, InstanceConnector> instanceConnectorEntry : instanceConnectorMap.entrySet()) {
            for (Instance instance : instanceConnectorEntry.getValue().getAllInstances()) {
                representationBuilder.add(
                        InstanceRepresentation.fromInstance(
                                instance.toBuilder()
                                        .setProvider(instanceConnectorEntry.getKey())
                                        .setHostname(dnsManager.getFullyQualifiedDomainName(instance))
                                        .setTags(tagManager.getTags(instance))
                                        .build(),
                                InstanceResource.constructSelfUri(uriInfo, instance.getId())));
            }
        }
        return Response.ok(representationBuilder.build()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createInstance(final InstanceCreationRequest request, @Context UriInfo uriInfo)
    {
        checkNotNull(request);
        checkNotNull(uriInfo);

        if (!instanceConnectorMap.containsKey(request.getProvider())) {
            return Response.status(Status.BAD_REQUEST).entity(new InstanceCreationFailedResponse(request, PROVIDER_UNAVAILABLE)).build();
        }

        if (instanceConnectorMap.get(request.getProvider()).getLocation(request.getLocation()) == null) {
            return Response.status(Status.BAD_REQUEST).entity(new InstanceCreationFailedResponse(request, LOCATION_UNAVAILABLE)).build();
        }

        if (!Iterables.any(instanceConnectorMap.get(request.getProvider()).getLocation(request.getLocation()).getAvailableSizes(), new Predicate<Size>()
        {
            @Override
            public boolean apply(@Nullable Size size)
            {
                return size.getSize().equals(request.getSize());
            }
        })) {
            return Response.status(Status.BAD_REQUEST).entity(new InstanceCreationFailedResponse(request, SIZE_UNAVAILABLE)).build();
        }

        String instanceId = instanceConnectorMap.get(request.getProvider()).createInstance(request.getSize(), request.getNamePrefix(), request.getLocation());

        for (InstanceCreationNotifier instanceCreationNotifier : instanceCreationNotifiers) {
            instanceCreationNotifier.notifyInstanceCreated(instanceId);
        }

        return Response.created(InstanceResource.constructSelfUri(uriInfo, instanceId)).build();
    }
}
