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

import static com.proofpoint.testing.EquivalenceTester.equivalenceTester;
import static org.testng.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: mattstep
 * Date: 10/13/11
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestInstance
{

    private JsonCodec<Instance> instanceJsonCodec = JsonCodec.jsonCodec(Instance.class);

    @Test
    public void testEquivalence()
    {
        equivalenceTester()
                .addEquivalentGroup(
                        new Instance("test1", "a", "aa", "aaa", "aaaa"),
                        new Instance("test1", "b", "aa", "aaa", "aaaa"),
                        new Instance("test1", "a", "bb", "aaa", "aaaa"),
                        new Instance("test1", "a", "aa", "bbb", "aaaa"),
                        new Instance("test1", "a", "aa", "aaa", "bbbb"))
                .addEquivalentGroup(
                        new Instance("test2", "a", "aa", "aaa", "aaaa"),
                        new Instance("test2", "a", "bb", "aaa", "aaaa"),
                        new Instance("test2", "b", "aa", "aaa", "aaaa"),
                        new Instance("test2", "a", "aa", "bbb", "aaaa"),
                        new Instance("test2", "a", "aa", "aaa", "bbbb"))
                .check();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullIdThrows()
    {
        new Instance(null, "a", "aa", "aaa", "aaaa");
    }

    @Test
    public void testJsonMarshalling()
    {
        Instance testInstance = new Instance("test1", "a", "aa", "aaa", "aaaa");
        String jsonInstance = instanceJsonCodec.toJson(testInstance);
        Instance encodedInstance = instanceJsonCodec.fromJson(jsonInstance);

        assertEquals(testInstance.getId(), encodedInstance.getId());
        assertEquals(testInstance.getSize(), encodedInstance.getSize());
        assertEquals(testInstance.getStatus(), encodedInstance.getStatus());
        assertEquals(testInstance.getHostname(), encodedInstance.getHostname());
    }
}
