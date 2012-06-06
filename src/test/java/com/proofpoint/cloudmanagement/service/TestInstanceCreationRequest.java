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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.proofpoint.json.JsonCodec;
import org.apache.bval.jsr303.ApacheValidationProvider;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Map;

import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.transform;
import static com.proofpoint.testing.Assertions.assertEqualsIgnoreOrder;
import static com.proofpoint.testing.EquivalenceTester.equivalenceTester;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestInstanceCreationRequest
{

    private JsonCodec<InstanceCreationRequest> icrJsonCodec = JsonCodec.jsonCodec(InstanceCreationRequest.class);
    private JsonCodec<Map<String, Object>> mapJsonCodec = JsonCodec.mapJsonCodec(String.class, Object.class);
    private static final Validator VALIDATOR = Validation.byProvider(ApacheValidationProvider.class).configure().buildValidatorFactory().getValidator();


    @Test
    public void testEquivalence()
    {
        equivalenceTester()
                .addEquivalentGroup(new InstanceCreationRequest("a", "aa", "aaa", "aaaa"), new InstanceCreationRequest("a", "aa", "aaa", "aaaa"))
                .addEquivalentGroup(new InstanceCreationRequest("b", "aa", "aaa", "aaaa"), new InstanceCreationRequest("b", "aa", "aaa", "aaaa"))
                .addEquivalentGroup(new InstanceCreationRequest("a", "bb", "aaa", "aaaa"), new InstanceCreationRequest("a", "bb", "aaa", "aaaa"))
                .addEquivalentGroup(new InstanceCreationRequest("a", "aa", "bbb", "aaaa"), new InstanceCreationRequest("a", "aa", "bbb", "aaaa"))
                .addEquivalentGroup(new InstanceCreationRequest("a", "aa", "aaa", "bbbb"), new InstanceCreationRequest("a", "aa", "aaa", "bbbb"))
                .check();
    }

    @Test
    public void testJsonMarshalling()
    {
        InstanceCreationRequest instanceCreationRequest = new InstanceCreationRequest("a", "aa", "aaa", "aaaa");
        String jsonInstanceCreationRequest = icrJsonCodec.toJson(instanceCreationRequest);

        Map<String, Object> mapEncodedInstanceCreationRequest = mapJsonCodec.fromJson(jsonInstanceCreationRequest);

        assertEquals(instanceCreationRequest.getProvider(), mapEncodedInstanceCreationRequest.get("provider"));
        assertEquals(instanceCreationRequest.getLocation(), mapEncodedInstanceCreationRequest.get("location"));
        assertEquals(instanceCreationRequest.getNamePrefix(), mapEncodedInstanceCreationRequest.get("namePrefix"));
        assertEquals(instanceCreationRequest.getSize(), mapEncodedInstanceCreationRequest.get("size"));

        InstanceCreationRequest encodedInstanceCreationRequest = icrJsonCodec.fromJson(jsonInstanceCreationRequest);

        assertEquals(instanceCreationRequest, encodedInstanceCreationRequest);
    }

    @Test
    public void testNotNullValidation()
    {
        InstanceCreationRequest instanceCreationRequest = new InstanceCreationRequest(null, null, null, null);

        assertTrue(all(VALIDATOR.validate(instanceCreationRequest), new Predicate<ConstraintViolation<InstanceCreationRequest>>()
        {
            @Override
            public boolean apply(@Nullable ConstraintViolation<InstanceCreationRequest> input)
            {
                return input.getMessage().equals("may not be null");
            }
        }));

        assertEqualsIgnoreOrder(
                ImmutableList.of("location", "namePrefix", "provider", "size"),
                transform(VALIDATOR.validate(instanceCreationRequest), new Function<ConstraintViolation<InstanceCreationRequest>, String>()
                {
                    @Override
                    public String apply(@Nullable ConstraintViolation<InstanceCreationRequest> input)
                    {
                        return input.getPropertyPath().toString();
                    }
                }));
    }
}
