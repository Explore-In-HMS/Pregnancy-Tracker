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

import org.json.JSONObject
import java.io.Serializable

class Profile : Serializable {

    //CLass Constants
    private val NAME = "name"
    private val EMAIL = "email"
    private val PHOTO_URL = "photo_url"

    //Class Variables
    var name: String
    var email: String
    var photoUrl: String

    constructor(name: String, email: String, photoUrl: String) {
        this.name = name
        this.email = email
        this.photoUrl = photoUrl
    }

    constructor(jsonString: String) {
        val obj = JSONObject(jsonString)
        name = obj.getString(NAME)
        email = obj.getString(EMAIL)
        photoUrl = obj.getString(PHOTO_URL)
    }

    override fun toString(): String {
        val obj = JSONObject()
        obj.put(NAME, name)
        obj.put(EMAIL, email)
        obj.put(PHOTO_URL, photoUrl)
        return obj.toString()
    }
}