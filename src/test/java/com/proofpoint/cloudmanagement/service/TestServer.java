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

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import com.proofpoint.configuration.ConfigurationFactory;
import com.proofpoint.configuration.ConfigurationModule;
import com.proofpoint.experimental.jmx.JmxHttpModule;
import com.proofpoint.http.server.testing.TestingHttpServer;
import com.proofpoint.http.server.testing.TestingHttpServerModule;
import com.proofpoint.jaxrs.JaxrsModule;
import com.proofpoint.jmx.JmxModule;
import com.proofpoint.json.JsonCodec;
import com.proofpoint.json.JsonModule;
import com.proofpoint.node.testing.TestingNodeModule;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class TestServer
{
    private AsyncHttpClient client;
    private TestingHttpServer server;

    @BeforeMethod
    public void setup()
            throws Exception
    {
        Injector injector = Guice.createInjector(
                new TestingNodeModule(),
                new TestingHttpServerModule(),
                new JsonModule(),
                new JaxrsModule(),
                new JmxHttpModule(),
                new JmxModule(),
                new MainModule(),
                new ConfigurationModule(new ConfigurationFactory(
                        ImmutableMap.<String, String>builder()
                                .put("nova.location", "http://localhost:8774")
                                .put("nova.user", "admin")
                                .put("nova.api-key", "admin-api-key")
                                .put("nova.default-image-id", "1")
                                .build())));

        server = injector.getInstance(TestingHttpServer.class);

        server.start();
        client = new AsyncHttpClient();
    }

    @AfterMethod
    public void teardown()
            throws Exception
    {
        if (server != null) {
            server.stop();
        }

        if (client != null) {
            client.close();
        }
    }

    private JsonCodec<Instance> instanceJsonCodec = JsonCodec.jsonCodec(Instance.class);
    private JsonCodec<List<Instance>> listJsonCodec = JsonCodec.listJsonCodec(instanceJsonCodec);


    @Test(enabled = false)
    public void testInstances()
            throws Exception
    {
        Response createResponse = client.preparePost(urlFor("/v1/instance?pretty"))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .setBody("{\"name\":\"ttylinux\",\"flavor_id\":\"7\"}").execute().get();
        assertEquals(createResponse.getStatusCode(), javax.ws.rs.core.Response.Status.OK.getStatusCode());
        System.err.println(createResponse.getResponseBody());
        Instance createdInstance = instanceJsonCodec.fromJson(createResponse.getResponseBody());
        String serverId = createdInstance.getId();


        boolean ready = false;
        while (ready) {
            Response listResponse1 = client.prepareGet(urlFor("/v1/instance?pretty")).execute().get();
            assertEquals(listResponse1.getStatusCode(), javax.ws.rs.core.Response.Status.OK.getStatusCode());
            List<Instance> instances = listJsonCodec.fromJson(listResponse1.getResponseBody());
            for (Instance instance : instances) {
                ready |= ((instance.getId() == serverId) && (instance.getStatus() != "BUILD"));
            }
        }

        Response deleteResponse = client.prepareDelete(urlFor("/v1/instance/" + serverId + "?pretty")).execute().get();
        assertEquals(deleteResponse.getStatusCode(), javax.ws.rs.core.Response.Status.NO_CONTENT.getStatusCode());

        Response listResponse2 = client.prepareGet(urlFor("/v1/instance?pretty")).execute().get();
        assertEquals(listResponse2.getStatusCode(), javax.ws.rs.core.Response.Status.OK.getStatusCode());
        //List<Map<String, String>> listResponseMap2 = listJsonCodec.fromJson(listResponse2.getResponseBody());

    }

    private String urlFor(String path)
    {
        return server.getBaseUrl().resolve(path).toString();
    }
}
