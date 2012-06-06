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

import com.google.common.base.Throwables;
import com.proofpoint.cloudmanagement.service.Instance;
import com.proofpoint.cloudmanagement.service.JCloudsInstanceConnector;
import com.proofpoint.cloudmanagement.service.TagManager;
import com.proofpoint.log.Logger;

import javax.inject.Inject;
import java.util.Collections;

public class InventoryTagManager implements TagManager
{
    private static final Logger log = Logger.get(JCloudsInstanceConnector.class);

    private final InventoryClient inventoryClient;

    @Inject
    public InventoryTagManager(InventoryClient inventoryClient)
    {
        this.inventoryClient = inventoryClient;
    }

    @Override
    public TagUpdateStatus addTag(Instance instance, String tag)
    {
        try {
            InventorySystem inventorySystem = inventoryClient.getSystem(instance.getHostname());
            if (inventorySystem == null) {
                return TagUpdateStatus.NOT_FOUND;
            }
            if (inventorySystem.addTag(tag)) {
                inventoryClient.patchSystem(inventorySystem);
            }
        }
        catch (Exception e) {
            log.error("Exception caught attempting to talk to inventory :", e);
            throw Throwables.propagate(e);
        }

        return TagUpdateStatus.UPDATED;
    }

    @Override
    public TagUpdateStatus deleteTag(Instance instance, String tag)
    {
        try {
            InventorySystem inventorySystem = inventoryClient.getSystem(instance.getHostname());
            if (inventorySystem == null) {
                return TagUpdateStatus.NOT_FOUND;
            }
            if (inventorySystem.deleteTag(tag)) {
                inventoryClient.patchSystem(inventorySystem);
            }
        }
        catch (Exception e) {
            log.error("Exception caught attempting to talk to inventory :", e);
            throw Throwables.propagate(e);
        }

        return TagUpdateStatus.UPDATED;
    }

    @Override
    public Iterable<String> getTags(Instance instance)
    {
        try {
            InventorySystem inventorySystem = inventoryClient.getSystem(instance.getHostname());
            if (inventorySystem == null) {
                return Collections.emptyList();
            }
            return inventorySystem.getTagList();
        }
        catch (Exception e) {
            log.error("Exception caught attempting to talk to inventory :", e);
            throw Throwables.propagate(e);
        }
    }
}