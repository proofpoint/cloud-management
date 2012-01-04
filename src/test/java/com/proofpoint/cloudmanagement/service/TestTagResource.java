package com.proofpoint.cloudmanagement.service;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.UUID;

import static org.testng.AssertJUnit.assertEquals;

public class TestTagResource
{
    private TagResource tagResource;
    private InMemoryInstanceConnector instanceConnector;

    @BeforeMethod
    public void setupResource()
    {
        instanceConnector = new InMemoryInstanceConnector();
        tagResource = new TagResource(instanceConnector);
    }

    @Test
    public void testAddTag()
    {
        Instance createdInstance = instanceConnector.createInstance("m1.tiny", "wyan");
        Response response1 = tagResource.addTag(createdInstance.getId(), "new tag");
        assertEquals(Status.OK.getStatusCode(), response1.getStatus());
        Response response2 = tagResource.addTag(UUID.randomUUID().toString(), "new tag");
        assertEquals(Status.NOT_FOUND.getStatusCode(), response2.getStatus());
    }

    @Test
    public void testDeleteTag()
    {
        Instance createdInstance = instanceConnector.createInstance("m1.tiny", "wyan");
        Response response1 = tagResource.deleteTag(createdInstance.getId(), "new tag");
        assertEquals(Status.NO_CONTENT.getStatusCode(), response1.getStatus());
        Response response2 = tagResource.deleteTag(UUID.randomUUID().toString(), "new tag");
        assertEquals(Status.NOT_FOUND.getStatusCode(), response2.getStatus());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testDeleteWithNullIdThrows()
    {
        tagResource.deleteTag(null, "new tag");
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testAddWithNullIdThrows()
    {
        tagResource.addTag(null, "new tag");
    }

}
