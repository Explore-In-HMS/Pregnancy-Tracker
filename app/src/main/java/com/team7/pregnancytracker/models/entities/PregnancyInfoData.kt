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

package com.team7.pregnancytracker.models.entities

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.utils.Constants
import java.io.Serializable
import java.util.*


@Entity(tableName = Constants.DB_TABLE_NAME_PREGNANCY_WEEKLY_INFO)
data class PregnancyInfoData(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Constants.DB_COLUMN_NAME_INFO_ID)
    var id: Long = 0,

    @ColumnInfo(name = Constants.DB_COLUMN_NAME_WEEK)
    var week: Int = 0,

    @ColumnInfo(name = Constants.DB_COLUMN_NAME_BABY_INFO)
    var babyInfo: String = "",

    @ColumnInfo(name = Constants.DB_COLUMN_NAME_MOM_INFO)
    var momInfo: String = "",

    @ColumnInfo(name = Constants.DB_COLUMN_NAME_BABY_SIZE)
    var babySize: String? = ""

) : Serializable
