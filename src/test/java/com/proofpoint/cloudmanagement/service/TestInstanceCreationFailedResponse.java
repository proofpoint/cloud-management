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

import com.proofpoint.cloudmanagement.service.InstanceCreationFailedResponse.InstanceCreationError;
import com.proofpoint.json.JsonCodec;
import com.proofpoint.testing.EquivalenceTester;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertEquals;

public class TestInstanceCreationFailedResponse
{

    JsonCodec<InstanceCreationFailedResponse> icfrCodec = JsonCodec.jsonCodec(InstanceCreationFailedResponse.class);
    JsonCodec<Map<String, Object>> mapJsonCodec = JsonCodec.mapJsonCodec(String.class, Object.class);
    JsonCodec<InstanceCreationRequest> icrCodec = JsonCodec.jsonCodec(InstanceCreationRequest.class);

    @Test
    public void testEquivalence()
    {
        InstanceCreationRequest request1 = new InstanceCreationRequest("a", "aa", "aaa", "aaaa");
        InstanceCreationRequest request2 = new InstanceCreationRequest("b", "bb", "bbb", "bbbb");

        EquivalenceTester.equivalenceTester()
                .addEquivalentGroup(new InstanceCreationFailedResponse(request1, InstanceCreationError.PROVIDER_UNAVAILABLE), new InstanceCreationFailedResponse(request1, InstanceCreationError.PROVIDER_UNAVAILABLE))
                .addEquivalentGroup(new InstanceCreationFailedResponse(request2, InstanceCreationError.PROVIDER_UNAVAILABLE), new InstanceCreationFailedResponse(request2, InstanceCreationError.PROVIDER_UNAVAILABLE))
                .addEquivalentGroup(new InstanceCreationFailedResponse(request1, InstanceCreationError.LOCATION_UNAVAILABLE), new InstanceCreationFailedResponse(request1, InstanceCreationError.LOCATION_UNAVAILABLE))
                .check();
    }

    @Test
    public void testJsonMarshalling()
    {
        InstanceCreationFailedResponse response = new InstanceCreationFailedResponse(new InstanceCreationRequest("a", "aa", "aaa", "aaaa"), InstanceCreationError.PROVIDER_UNAVAILABLE);
        Map<String, Object> encodedMapResponse = mapJsonCodec.fromJson(icfrCodec.toJson(response));

        assertEquals(mapJsonCodec.fromJson(icrCodec.toJson(response.getRequestedInstance())), encodedMapResponse.get("requestedInstance"));
        assertEquals(response.getErrorMessage(), encodedMapResponse.get("error"));
    }
}
