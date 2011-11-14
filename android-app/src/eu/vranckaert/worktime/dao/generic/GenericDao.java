/*
 *  Copyright 2011 Dirk Vranckaert
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package eu.vranckaert.worktime.dao.generic;

import java.util.List;

public interface GenericDao<T, ID> {
    /**
     * Find an entity by it identifier.
     * @param id The identifier.
     * @return The entity.
     */
    T findById(ID id);

    /**
     * Find all entities of one type.
     * @return A list of entities.
     */
    List<T> findAll();

    /**
     * Persists a new entity in the datbase or updates an already existing one.
     * @param entity The entity to store or update.
     * @return The stored entity.
     */
    T save(T entity);

    /**
     * Persists a new entity in the datbase or updates an already existing one.
     * @param entity The entity to store or update.
     * @return The stored entity.
     */
    T update(T entity);

    /**
     * Removes an entity.
     * @param entity The entity to remove.
     */
    void delete(T entity);

    /**
     * Refreshes the content of an object based on it's identifier.
     * @param entity The entity to refresh.
     * @return The number of entities refreshed. If everything is ok this should always be one!
     */
    int refresh(T entity);
}