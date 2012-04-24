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
