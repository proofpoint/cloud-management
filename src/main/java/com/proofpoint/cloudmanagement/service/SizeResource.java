package com.proofpoint.cloudmanagement.service;

import com.google.common.base.Preconditions;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v1/size")
public class SizeResource
{
    private final InstanceConnector instanceConnector;

    @Inject
    public SizeResource(InstanceConnector instanceConnector)
    {
        Preconditions.checkNotNull(instanceConnector);

        this.instanceConnector = instanceConnector;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSizes()
    {
        return Response.ok(instanceConnector.getSizes()).build();
    }
}
