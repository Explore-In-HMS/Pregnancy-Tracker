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

package com.team7.pregnancytracker.models.interfaces

import com.team7.pregnancytracker.models.entities.GeocodingResponse
import com.team7.pregnancytracker.models.entities.SiteGeo
import retrofit2.Call
import retrofit2.http.*

interface PostService {



        @Headers("Content-Type: application/json; charset=UTF-8")
        @POST("reverseGeocode?key=CgB6e3x9nhUqZMpZ8E%2FT%2Bbl%2BYzR3NvXOvovrpAMcHh2mMw0arupdLG4t9ajJc3DFQ%2FMs2oSP%2BuMy%2Fl4%2FMKRoZ7nH")
        fun listPost ( @Body geocodingResponse: GeocodingResponse):Call<SiteGeo>


}