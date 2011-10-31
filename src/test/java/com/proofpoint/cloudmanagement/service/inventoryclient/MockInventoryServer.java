package com.proofpoint.cloudmanagement.service.inventoryclient;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Path("/inv_api/v1")
public class MockInventoryServer
{
    private static final String NAME_KEY = "fqdn";
    private static final String INSTANCE_ID_KEY = "serial_number";

    private final Set<Map<String, String>> inventory = Sets.newHashSet();

    public static class MockInventoryServerModule
        implements Module
    {
        @Override
        public void configure(Binder binder)
        {
            binder.bind(MockInventoryServer.class).in(Scopes.SINGLETON);
        }
    }

    @GET
    @Path("/pcmsystemname/{instanceId}")
    public Map<String, String> getPcmName(@PathParam("instanceId") String instanceId)
    {
        Map<String, String> system = systemWithInstanceId(instanceId);
        if (system == null) {
            system = Maps.newHashMap();
            system.put(NAME_KEY, UUID.randomUUID().toString());
            system.put(INSTANCE_ID_KEY, instanceId);
            inventory.add(system);
        }
        return ImmutableMap.of(NAME_KEY, system.get(NAME_KEY));
    }

    @GET
    @Path("/system/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSystem(@PathParam("name") String name)
    {
        Map<String, String> system = systemWithName(name);
        if (system == null) {
            // Inventory returns no content if the system doesn't exist
            return Response.noContent().build();
        }

        return Response.ok(system).build();
    }

    @PUT
    @Path("/system/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response patchSystem(@PathParam("name") String name, Map<String, String> patchSource)
    {
        Map<String, String> system = systemWithName(name);
        if (system == null) {
            // Inventory errors out when trying to put to a non-existent system
            return Response
                    .serverError()
                    .entity(ImmutableSet.of("\"1452: Cannot add or update a child row: a foreign key constraint fails (`inventory/device_metadata`, CONSTRAINT `fk_device_metadata_device_fqdn` FOREIGN KEY (`fqdn`) REFERENCES `device` (`fqdn`) ON DELETE CASCADE ON UPDATE CASCADE)\""))
                    .build();
        }

        system.putAll(patchSource);
        return Response.noContent().build();
    }

    private Map<String, String> systemWithName(final String name)
    {
        if (name == null) {
            return null;
        }
        return systemWithWithPredicate(new Predicate<Map<String, String>>()
        {
            @Override
            public boolean apply(@Nullable Map<String, String> input)
            {
                return name.equals(input.get(NAME_KEY));
            }
        });
    }

    private Map<String, String> systemWithInstanceId(final String id)
    {
        if (id == null) {
            return null;
        }
        return systemWithWithPredicate(new Predicate<Map<String, String>>()
        {
            @Override
            public boolean apply(@Nullable Map<String, String> input)
            {
                return id.equals(input.get(INSTANCE_ID_KEY));
            }
        });
    }

    private Map<String, String> systemWithWithPredicate(Predicate<Map<String, String>> predicate)
    {
        Preconditions.checkNotNull(predicate);

        Set<Map<String, String>> filtered = Sets.filter(inventory, predicate);

        if (filtered.size() > 1) {
            throw new IllegalStateException("Too many objects matching predicate in set");
        }

        if (filtered.isEmpty()) {
            return null;
        }

        return filtered.iterator().next();
    }
}
