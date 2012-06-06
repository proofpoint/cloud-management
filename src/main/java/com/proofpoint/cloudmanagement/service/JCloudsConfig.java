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

import com.proofpoint.configuration.Config;

import javax.validation.constraints.NotNull;

public class JCloudsConfig
{
    private String location;
    private String user;
    private String secret;
    private String defaultImageId;
    private String name;
    private String api;
    private String awsVpcSubnetId;

    @Config("api")
    public JCloudsConfig setApi(String api)
    {
        this.api = api;
        return this;
    }

    @Config("name")
    public JCloudsConfig setName(String name)
    {
        this.name = name;
        return this;
    }

    @Config("location")
    public JCloudsConfig setLocation(String location)
    {
        this.location = location;
        return this;
    }

    @Config("user")
    public JCloudsConfig setUser(String user)
    {
        this.user = user;
        return this;
    }

    @Config("secret")
    public JCloudsConfig setSecret(String secret)
    {
        this.secret = secret;
        return this;
    }

    @Config("default-image-id")
    public JCloudsConfig setDefaultImageId(String defaultImageId)
    {
        this.defaultImageId = defaultImageId;
        return this;
    }

    @Config("aws-vpc-subnet-id")
    public JCloudsConfig setAwsVpcSubnetId(String awsVpcSubnetId)
    {
        this.awsVpcSubnetId = awsVpcSubnetId;
        return this;
    }

    public String getLocation()
    {
        return this.location;
    }

    @NotNull
    public String getUser()
    {
        return this.user;
    }

    @NotNull
    public String getSecret()
    {
        return this.secret;
    }

    @NotNull
    public String getDefaultImageId()
    {
        return defaultImageId;
    }

    @NotNull
    public String getName()
    {
        return name;
    }

    @NotNull
    public String getApi()
    {
        return api;
    }

    public String getAwsVpcSubnetId()
    {
        return awsVpcSubnetId;
    }
}
