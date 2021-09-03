/*
 * *
 *  *Copyright 2020. Explore in HMS. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 *
 *
 *
 */

package com.team7.pregnancytracker.models.daos

import androidx.room.*
import com.team7.pregnancytracker.models.entities.Memory
import com.team7.pregnancytracker.utils.Constants

@Dao
interface MemoryDao {
    @Insert
    fun insert(memory: Memory): Long

    @Delete
    fun delete(memory: Memory)

    @Update
    fun update(memory: Memory)

    @Query("SELECT * FROM " + Constants.DB_TABLE_NAME_MEMORY)
    fun getAllMemories(): List<Memory>
}