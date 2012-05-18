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
package com.proofpoint.cloudmanagement.service.inventoryclient;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.proofpoint.http.client.HttpClient;
import com.proofpoint.http.client.JsonBodyGenerator;
import com.proofpoint.http.client.JsonResponseHandler;
import com.proofpoint.http.client.Request;
import com.proofpoint.http.client.StatusResponseHandler;
import com.proofpoint.http.client.StatusResponseHandler.StatusResponse;
import com.proofpoint.json.JsonCodec;
import com.proofpoint.log.Logger;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;

import org.jclouds.encryption.internal.Base64;

import static com.proofpoint.http.client.Request.Builder.prepareGet;
import static com.proofpoint.http.client.Request.Builder.preparePut;

public class InventoryClient
{
    private final HttpClient client;
    private final URI inventoryHost;
    private final String authorization;

    private static final Logger log = Logger.get(InventoryClient.class);

    private static final JsonCodec<InventorySystem> SYSTEM_DATA_CODEC = JsonCodec.jsonCodec(InventorySystem.class);
    private static final JsonCodec<Map<String, String>> MAP_JSON_CODEC = JsonCodec.mapJsonCodec(String.class, String.class);

    @Inject
    public InventoryClient(InventoryClientConfig config, @Inventory HttpClient client)
    {
        this.inventoryHost = config.getInventoryUri();
        this.authorization = basicAuthEncode(config.getUserId(), config.getPassword());

        this.client = client;
    }

    public String getPcmSystemName(String instanceId)
            throws Exception
    {
        Preconditions.checkNotNull(instanceId, "instanceId is null");

        Request request = prepareGet()
                        .setUri(UriBuilder.fromUri(inventoryHost).path("/pcmsystemname/{instanceId}").build(instanceId))
                        .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                        .build();

        Map<String, String> response = client.execute(request, JsonResponseHandler.createJsonResponseHandler(MAP_JSON_CODEC));
        return response.get("fqdn");
    }

    public InventorySystem getSystem(String systemName)
            throws Exception
    {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(systemName), "systemName is required");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(authorization), "authToken is required");

        Request request = prepareGet()
                        .setUri(UriBuilder.fromUri(inventoryHost).path("/system/{system}").build(systemName))
                        .setHeader("Authorization", authorization)
                        .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                        .build();
        try {
            return client.execute(request, JsonResponseHandler.createJsonResponseHandler(SYSTEM_DATA_CODEC));
        }
        catch(Exception e) {
            performRequestAndValidateStatusForBodylessRequests(request);
        }
        return null;
    }

    public void patchSystem(InventorySystem inventorySystem)
            throws Exception
    {
        Preconditions.checkNotNull(inventorySystem, "inventorySystem is null");

        Request request = preparePut()
                        .setUri(UriBuilder.fromUri(inventoryHost).path("/system/{system}").build(inventorySystem.getFqdn()))
                        .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .setBodyGenerator(JsonBodyGenerator.jsonBodyGenerator(SYSTEM_DATA_CODEC, inventorySystem))
                        .build();

        log.info("Patch Request To Inventory [" + request + "] with object [" + inventorySystem + "]");

        performRequestAndValidateStatusForBodylessRequests(request);
    }

    private void performRequestAndValidateStatusForBodylessRequests(Request request)
    {
        StatusResponse response = client.execute(request, StatusResponseHandler.createStatusResponseHandler());
        if(!ImmutableList.of(200, 204).contains(response.getStatusCode())) {
            throw new RuntimeException(String.format("Request failed with code %d: Body -->|%s|<--", response.getStatusCode(), response.getStatusMessage()));
        }
    }

    @VisibleForTesting
    static String basicAuthEncode(String user, String pass)
    {
        return String.format("Basic %s",
                Base64.encodeBytes(String.format("%s:%s", user, pass).getBytes(Charsets.UTF_8)));
    }
}

