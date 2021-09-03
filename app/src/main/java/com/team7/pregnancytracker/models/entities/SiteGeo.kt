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

import com.google.gson.annotations.SerializedName

data class SiteGeo(

	@field:SerializedName("returnCode")
	val returnCode: String? = null,

	@field:SerializedName("sites")
	val sites: List<SitesItem?>? = null,

	@field:SerializedName("returnDesc")
	val returnDesc: String? = null
)

data class Southwest(

	@field:SerializedName("lng")
	val lng: Double? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)

data class Viewport(

	@field:SerializedName("southwest")
	val southwest: Southwest? = null,

	@field:SerializedName("northeast")
	val northeast: Northeast? = null
)

data class Address(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("countryCode")
	val countryCode: String? = null,

	@field:SerializedName("adminArea")
	val adminArea: String? = null,

	@field:SerializedName("subAdminArea")
	val subAdminArea: String? = null,

	@field:SerializedName("locality")
	val locality: String? = null
)

data class Location(

	@field:SerializedName("lng")
	val lng: Double? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)

data class SitesItem(

	@field:SerializedName("formatAddress")
	val formatAddress: String? = null,

	@field:SerializedName("address")
	val address: Address? = null,

	@field:SerializedName("viewport")
	val viewport: Viewport? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("siteId")
	val siteId: String? = null,

	@field:SerializedName("location")
	val location: Location? = null
)

data class Northeast(

	@field:SerializedName("lng")
	val lng: Double? = null,

	@field:SerializedName("lat")
	val lat: Double? = null
)
