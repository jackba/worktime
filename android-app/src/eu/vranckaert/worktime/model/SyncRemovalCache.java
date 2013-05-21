/*
 * Copyright 2013 Dirk Vranckaert
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

package eu.vranckaert.worktime.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * User: Dirk Vranckaert
 * Date: 11/01/13
 * Time: 09:42
 */
@DatabaseTable
public class SyncRemovalCache {
    @DatabaseField(id = true, generatedId = false)
    private String syncKey;
    @DatabaseField
    private String entityName;

    public SyncRemovalCache() {}

    public SyncRemovalCache(String syncKey, String entityName) {
        this.syncKey = syncKey;
        this.entityName = entityName;
    }

    public SyncRemovalCache(String syncKey) {
        this.syncKey = syncKey;
    }

    public String getSyncKey() {
        return syncKey;
    }

    public void setSyncKey(String syncKey) {
        this.syncKey = syncKey;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
