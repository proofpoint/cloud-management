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
