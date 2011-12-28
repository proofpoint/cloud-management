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

import com.proofpoint.json.JsonCodec;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

import static com.proofpoint.testing.EquivalenceTester.equivalenceTester;
import static org.testng.Assert.assertEquals;

public class TestInstanceRepresentation
{

    private JsonCodec<InstanceRepresentation> instanceRepresentationJsonCodec = JsonCodec.jsonCodec(InstanceRepresentation.class);
    private JsonCodec<Map<String, Object>> mapJsonCodec = JsonCodec.mapJsonCodec(String.class, Object.class);

    @Test
    public void testEquivalence()
            throws URISyntaxException
    {
        equivalenceTester()
                .addEquivalentGroup(
                        new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", Arrays.asList("aaa"), new URI("http://foo")),
                        new InstanceRepresentation("test1", "b", "aa", "aaa", "aaaa", Arrays.asList("bbb"), new URI("http://foo")),
                        new InstanceRepresentation("test1", "a", "bb", "aaa", "aaaa", Arrays.asList("ccc"), new URI("http://foo")),
                        new InstanceRepresentation("test1", "a", "aa", "bbb", "aaaa", Arrays.asList("ddd"), new URI("http://foo")),
                        new InstanceRepresentation("test1", "a", "aa", "aaa", "bbbb", Arrays.asList("eee"), new URI("http://foo")),
                        new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", Arrays.asList("aaa"), new URI("http://bar")))
                .addEquivalentGroup(
                        new InstanceRepresentation("test2", "a", "aa", "aaa", "aaaa", Arrays.asList("aaa"), new URI("http://foo")),
                        new InstanceRepresentation("test2", "a", "bb", "aaa", "aaaa", Arrays.asList("bbb"), new URI("http://foo")),
                        new InstanceRepresentation("test2", "b", "aa", "aaa", "aaaa", Arrays.asList("ccc"), new URI("http://foo")),
                        new InstanceRepresentation("test2", "a", "aa", "bbb", "aaaa", Arrays.asList("ddd"), new URI("http://foo")),
                        new InstanceRepresentation("test2", "a", "aa", "aaa", "bbbb", Arrays.asList("eee"), new URI("http://foo")),
                        new InstanceRepresentation("test2", "a", "aa", "aaa", "aaaa", Arrays.asList("aaa"), new URI("http://bar")))
                .check();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullIdThrows()
            throws URISyntaxException
    {
        new InstanceRepresentation(null, "a", "aa", "aaa", "aaaa", Arrays.asList("aaa"), new URI("http://foo"));
    }

    @Test
    public void testJsonMarshalling()
            throws URISyntaxException
    {
        InstanceRepresentation instanceRepresentation = new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa",
                Arrays.asList("aaa", "bbb"), new URI("http://foo"));
        String jsonInstanceRepresentation = instanceRepresentationJsonCodec.toJson(instanceRepresentation);
        Map<String, Object> encodedInstanceRepresentation = mapJsonCodec.fromJson(jsonInstanceRepresentation);

        assertEquals(instanceRepresentation.getId(), encodedInstanceRepresentation.get("id"));
        assertEquals(instanceRepresentation.getName(), encodedInstanceRepresentation.get("name"));
        assertEquals(instanceRepresentation.getSize(), encodedInstanceRepresentation.get("size"));
        assertEquals(instanceRepresentation.getStatus(), encodedInstanceRepresentation.get("status"));
        assertEquals(instanceRepresentation.getHostname(), encodedInstanceRepresentation.get("hostname"));
        assertEquals(instanceRepresentation.getTags(), encodedInstanceRepresentation.get("tags"));
        assertEquals(instanceRepresentation.getSelf().toString(), encodedInstanceRepresentation.get("self").toString());
    }
}
