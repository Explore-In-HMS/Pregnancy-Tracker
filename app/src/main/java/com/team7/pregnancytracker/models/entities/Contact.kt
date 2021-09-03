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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.team7.pregnancytracker.utils.Constants
import java.io.Serializable

@Entity(tableName = Constants.DB_TABLE_NAME_CONTACT)
data class Contact(

    @ColumnInfo(name = Constants.DB_COLUMN_NAME_TITLE)
    var name: String,

    @ColumnInfo(name = Constants.DB_COLUMN_NAME_TYPE)
    var number: String,

    @ColumnInfo(name = Constants.DB_COLUMN_NAME_CREATE_DATE)
    var createDate: Long


) : Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Constants.DB_COLUMN_NAME_ID)
    var id: Long = 0

}
