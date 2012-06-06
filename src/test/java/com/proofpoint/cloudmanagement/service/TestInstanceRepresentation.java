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
                .addEquivalentGroup(new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")), new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")))
                .addEquivalentGroup(new InstanceRepresentation("test2", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")), new InstanceRepresentation("test2", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")))
                .addEquivalentGroup(new InstanceRepresentation("test1", "b", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")), new InstanceRepresentation("test1", "b", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")))
                .addEquivalentGroup(new InstanceRepresentation("test1", "a", "bb", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")), new InstanceRepresentation("test1", "a", "bb", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")))
                .addEquivalentGroup(new InstanceRepresentation("test1", "a", "aa", "bbb", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")), new InstanceRepresentation("test1", "a", "aa", "bbb", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")))
                .addEquivalentGroup(new InstanceRepresentation("test1", "a", "aa", "aaa", "bbbb", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")), new InstanceRepresentation("test1", "a", "aa", "aaa", "bbbb", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")))
                .addEquivalentGroup(new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", "bbbbb", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")), new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", "bbbbb", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo")))
                .addEquivalentGroup(new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", "aaaaa", "bbbbbb", Arrays.asList("aaaaaaa"), new URI("http://foo")), new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", "aaaaa", "bbbbbb", Arrays.asList("aaaaaaa"), new URI("http://foo")))
                .addEquivalentGroup(new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("bbbbbbb"), new URI("http://foo")), new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("bbbbbbb"), new URI("http://foo")))
                .addEquivalentGroup(new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://bar")), new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://bar")))
                .check();
    }

    @Test
    public void testJsonMarshalling()
            throws URISyntaxException
    {
        InstanceRepresentation instanceRepresentation = new InstanceRepresentation("test1", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"), new URI("http://foo"));
        String jsonInstanceRepresentation = instanceRepresentationJsonCodec.toJson(instanceRepresentation);
        Map<String, Object> encodedInstanceRepresentation = mapJsonCodec.fromJson(jsonInstanceRepresentation);

        assertEquals(instanceRepresentation.getName(), encodedInstanceRepresentation.get("name"));
        assertEquals(instanceRepresentation.getSize(), encodedInstanceRepresentation.get("size"));
        assertEquals(instanceRepresentation.getStatus(), encodedInstanceRepresentation.get("status"));
        assertEquals(instanceRepresentation.getProvider(), encodedInstanceRepresentation.get("provider"));
        assertEquals(instanceRepresentation.getLocation(), encodedInstanceRepresentation.get("location"));
        assertEquals(instanceRepresentation.getHostname(), encodedInstanceRepresentation.get("hostname"));
        assertEquals(instanceRepresentation.getTags(), encodedInstanceRepresentation.get("tags"));
        assertEquals(instanceRepresentation.getSelf().toString(), encodedInstanceRepresentation.get("self").toString());
    }
}
