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
import com.proofpoint.configuration.testing.ConfigAssertions;
import org.testng.annotations.Test;

public class TestJCloudsConfig
{

    @Test
    public void testDefaults()
    {
        ConfigAssertions.assertRecordedDefaults(ConfigAssertions.recordDefaults(JCloudsConfig.class)
                .setSecret(null)
                .setLocation(null)
                .setUser(null)
                .setDefaultImageId(null)
                .setDefaultLocationId(null));
    }

    @Test
    public void testExplicitPropertyMappings()
    {
        ConfigAssertions.assertFullMapping(
                ImmutableMap.<String, String>builder()
                        .put("jclouds.location", "http://localhost:8774")
                        .put("jclouds.user", "user")
                        .put("jclouds.secret", "secret")
                        .put("jclouds.default-image-id", "default-image-id")
                        .put("jclouds.default-location-id", "default-location-id")
                        .build(),
                new JCloudsConfig()
                        .setLocation("http://localhost:8774")
                        .setUser("user")
                        .setSecret("secret")
                        .setDefaultImageId("default-image-id")
                        .setDefaultLocationId("default-location-id"));
    }

}
