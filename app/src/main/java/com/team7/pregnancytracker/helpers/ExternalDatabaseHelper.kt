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
import com.team7.pregnancytracker.models.daos.PregnancyInfoDao
import com.team7.pregnancytracker.models.entities.PregnancyInfoData
import com.team7.pregnancytracker.utils.Constants

@Database(entities = [PregnancyInfoData::class], version = 1)
abstract class ExternalDatabaseHelper : RoomDatabase() {

    abstract fun pregnancyInfoDao(): PregnancyInfoDao

    companion object {

        private var instance: ExternalDatabaseHelper? = null

        fun getDatabaseHelper(context: Context): ExternalDatabaseHelper? {

            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    ExternalDatabaseHelper::class.java,
                    Constants.EXTERNAL_DB_NAME
                ).createFromAsset("databases/" + Constants.EXTERNAL_DB_NAME)
                    .allowMainThreadQueries()
                    .build()
            }
            return instance
        }
    }
}