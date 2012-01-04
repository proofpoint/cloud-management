package com.proofpoint.cloudmanagement.service;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static com.proofpoint.testing.Assertions.assertEqualsIgnoreOrder;
import static org.testng.Assert.assertEquals;

public class TestSizeResource
{
    private SizeResource sizeResource;
    private InMemoryInstanceConnector inMemoryInstanceConnector;

    @BeforeMethod
    public void setupResource()
    {
        inMemoryInstanceConnector = new InMemoryInstanceConnector();
        sizeResource = new SizeResource(inMemoryInstanceConnector);
    }

    @Test
    public void testGetSizes()
    {
        Response response = sizeResource.getSizes();

        assertEquals(response.getStatus(), Status.OK.getStatusCode());
        assertEqualsIgnoreOrder((Iterable<Size>) response.getEntity(), inMemoryInstanceConnector.getSizes());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullInstanceConnectorThrows()
    {
        new SizeResource(null);
    }
}
