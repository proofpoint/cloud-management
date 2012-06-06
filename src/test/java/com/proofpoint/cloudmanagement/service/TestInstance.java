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

import org.testng.annotations.Test;

import java.util.Arrays;

import static com.proofpoint.testing.EquivalenceTester.equivalenceTester;

public class TestInstance
{
    @Test
    public void testEquivalence()
    {
        equivalenceTester()
                .addEquivalentGroup(
                        new Instance("test1", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa")),
                        new Instance("test1", "b", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa")),
                        new Instance("test1", "a", "bb", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa")),
                        new Instance("test1", "a", "aa", "bbb", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa")),
                        new Instance("test1", "a", "aa", "aaa", "bbbb", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa")),
                        new Instance("test1", "a", "aa", "aaa", "aaaa", "bbbbb", "aaaaaa", Arrays.asList("aaaaaaa")),
                        new Instance("test1", "a", "aa", "aaa", "aaaa", "aaaaa", "bbbbbb", Arrays.asList("aaaaaaa")),
                        new Instance("test1", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("bbbbbbb")))
                .addEquivalentGroup(
                        new Instance("test2", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa")),
                        new Instance("test2", "b", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa")),
                        new Instance("test2", "a", "bb", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa")),
                        new Instance("test2", "a", "aa", "bbb", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa")),
                        new Instance("test2", "a", "aa", "aaa", "bbbb", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa")),
                        new Instance("test2", "a", "aa", "aaa", "aaaa", "bbbbb", "aaaaaa", Arrays.asList("aaaaaaa")),
                        new Instance("test2", "a", "aa", "aaa", "aaaa", "aaaaa", "bbbbbb", Arrays.asList("aaaaaaa")),
                        new Instance("test2", "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("bbbbbbb")))
                .check();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNullIdThrows()
    {
        new Instance(null, "a", "aa", "aaa", "aaaa", "aaaaa", "aaaaaa", Arrays.asList("aaaaaaa"));
    }
}
