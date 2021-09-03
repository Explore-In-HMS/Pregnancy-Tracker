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

class MarkerType {
    var lat: Double
    var lang: Double
    var poitype: String?
    var sitename:String?
    var sitephonenumber:String? = null
    var siteadress:String?=null

    constructor(lat: Double, lang: Double, poitype: String?,sitename:String?,sitephonenumber:String?,siteadress:String?) {
        this.lat = lat
        this.lang = lang
        this.poitype = poitype
        this.sitename=sitename
        this.sitephonenumber=sitephonenumber
        this.siteadress=siteadress

    }

    constructor(lat: Double, lang: Double, poitype: String?, sitename: String?){
        this.lat = lat
        this.lang = lang
        this.poitype = poitype
        this.sitename=sitename
    }

    override fun toString(): String {
        return "Marker(lat=$lat, lang=$lang, poitype=$poitype)"
    }


}