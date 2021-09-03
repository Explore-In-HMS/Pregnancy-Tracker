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

package com.team7.pregnancytracker.helpers

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.team7.pregnancytracker.models.daos.ContactDao
import com.team7.pregnancytracker.models.daos.DiaryDao
import com.team7.pregnancytracker.models.daos.MemoryDao
import com.team7.pregnancytracker.models.entities.Contact
import com.team7.pregnancytracker.models.entities.Diary
import com.team7.pregnancytracker.models.entities.Memory
import com.team7.pregnancytracker.utils.Constants

@Database(entities = [Diary::class,Memory::class,Contact::class], version = 1)
abstract class DatabaseHelper : RoomDatabase() {

    abstract fun diaryDao(): DiaryDao
    abstract fun memoryDao():MemoryDao
    abstract fun contactDao():ContactDao
    companion object {

        private var instance: DatabaseHelper? = null

        fun getDatabaseHelper(context: Context): DatabaseHelper? {

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    DatabaseHelper::class.java,
                    Constants.DB_NAME
                ).allowMainThreadQueries()
                    .build()
            }
            return instance
        }
    }
}