package com.proofpoint.cloudmanagement.service;

import com.google.common.base.Strings;
import com.proofpoint.jaxrs.testing.MockUriInfo;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.UUID;

import static org.testng.Assert.assertEquals;

public class TestInstanceResource
{
    private InstanceResource instanceResource;
    private InMemoryInstanceConnector inMemoryInstanceConnector;

    private static final UriInfo INSTANCE_URI_INFO = MockUriInfo.from("http://localhost/v1/instance");

    @BeforeMethod
    public void setupResource()
    {
        inMemoryInstanceConnector = new InMemoryInstanceConnector();
        instanceResource = new InstanceResource(inMemoryInstanceConnector);
    }

    @Test
    public void testGetInstance()
    {
        Instance createdInstance = inMemoryInstanceConnector.createInstance("m1.tiny", "mattstep");
        InstanceRepresentation createdInstanceRepresentation = InstanceRepresentation.fromInstance(createdInstance, InstanceResource.constructSelfUri(INSTANCE_URI_INFO, createdInstance.getId()));

        Response response = instanceResource.getInstance(createdInstance.getId(), INSTANCE_URI_INFO);

        assertEquals(response.getStatus(), Status.OK.getStatusCode());
        assertEquals(response.getEntity(), createdInstanceRepresentation);
    }

    @Test
    public void testMissingGetInstance()
    {
        Response response = instanceResource.getInstance(UUID.randomUUID().toString(), INSTANCE_URI_INFO);

        assertEquals(response.getStatus(), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testDeleteInstance()
    {
        Instance createdInstance = inMemoryInstanceConnector.createInstance("m1.tiny", "mattstep");

        Response getResponse1 = instanceResource.getInstance(createdInstance.getId(), INSTANCE_URI_INFO);

        assertEquals(getResponse1.getStatus(), Status.OK.getStatusCode());

        Response deleteResponse = instanceResource.deleteInstance(createdInstance.getId());

        assertEquals(deleteResponse.getStatus(), Status.NO_CONTENT.getStatusCode());

        Response getResponse2 = instanceResource.getInstance(createdInstance.getId(), INSTANCE_URI_INFO);

        assertEquals(getResponse2.getStatus(), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testDeleteMissingInstance()
    {
        Response deleteResponse = instanceResource.deleteInstance(UUID.randomUUID().toString());

        assertEquals(deleteResponse.getStatus(), Status.NOT_FOUND.getStatusCode());
    }

    @Test
    public void testConstructSelfUri()
    {
        String id = UUID.randomUUID().toString();
        URI self = InstanceResource.constructSelfUri(INSTANCE_URI_INFO, id);

        assertEquals(id, Strings.commonSuffix(id, self.getPath()));
        assertEquals(INSTANCE_URI_INFO.getAbsolutePath().toString(), Strings.commonPrefix(INSTANCE_URI_INFO.getAbsolutePath().toString(), self.toString()));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testDeleteWithNullIdThrows()
    {
        instanceResource.deleteInstance(null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetWithNullIdThrows()
    {
        instanceResource.getInstance(null, INSTANCE_URI_INFO);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testGetWithNullUriInfoThrows()
    {
        instanceResource.getInstance(UUID.randomUUID().toString(), null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConstructionWithNullInstanceConnectorThrows()
    {
        new InstanceResource(null);
    }
}
