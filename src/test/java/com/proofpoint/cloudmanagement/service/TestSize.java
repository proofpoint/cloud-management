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
import com.proofpoint.units.DataSize;
import com.proofpoint.units.DataSize.Unit;
import org.testng.annotations.Test;

import java.util.Map;

import static com.proofpoint.testing.EquivalenceTester.equivalenceTester;
import static org.testng.Assert.assertEquals;

public class TestSize
{

    private JsonCodec<Size> sizeJsonCodec = JsonCodec.jsonCodec(Size.class);
    private JsonCodec<Map<String, Object>> mapJsonCodec = JsonCodec.mapJsonCodec(String.class, Object.class);

    @Test
    public void testEquivalence()
    {
        equivalenceTester()
                .addEquivalentGroup(new Size("a", 1, new DataSize(1, Unit.BYTE), new DataSize(1, Unit.KILOBYTE)),
                        new Size("a", 1, new DataSize(1, Unit.BYTE), new DataSize(1, Unit.KILOBYTE)))
                .addEquivalentGroup(new Size("b", 1, new DataSize(1, Unit.BYTE), new DataSize(1, Unit.KILOBYTE)),
                        new Size("b", 1, new DataSize(1, Unit.BYTE), new DataSize(1, Unit.KILOBYTE)))
                .addEquivalentGroup(new Size("a", 2, new DataSize(1, Unit.BYTE), new DataSize(1, Unit.KILOBYTE)),
                        new Size("a", 2, new DataSize(1, Unit.BYTE), new DataSize(1, Unit.KILOBYTE)))
                .addEquivalentGroup(new Size("a", 1, new DataSize(2, Unit.BYTE), new DataSize(1, Unit.KILOBYTE)),
                        new Size("a", 1, new DataSize(2, Unit.BYTE), new DataSize(1, Unit.KILOBYTE)))
                .addEquivalentGroup(new Size("a", 1, new DataSize(1, Unit.BYTE), new DataSize(2, Unit.KILOBYTE)),
                        new Size("a", 1, new DataSize(1, Unit.BYTE), new DataSize(2, Unit.KILOBYTE)))
                .check();
    }

    @Test
    public void testJsonMarshalling()
    {
        Size testSize = new Size("m1.xlarge", 8, new DataSize(8, Unit.GIGABYTE), new DataSize(1, Unit.TERABYTE));
        String json = sizeJsonCodec.toJson(testSize);
        Map<String, Object> encodedSize = mapJsonCodec.fromJson(json);

        assertEquals(encodedSize.get("size"), testSize.getSize());
        assertEquals(encodedSize.get("cores"), testSize.getCores());
        assertEquals(encodedSize.get("memory"), testSize.getMemory());
        assertEquals(encodedSize.get("disk"), testSize.getDisk());
    }
}
