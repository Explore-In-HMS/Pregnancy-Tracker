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

package com.team7.pregnancytracker.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import com.huawei.hms.maps.HuaweiMap
import com.huawei.hms.maps.model.Marker
import com.team7.pregnancytracker.R
import kotlinx.android.synthetic.main.activity_custom_info_window.view.*


internal class CustomInfoWindow(val context: Context) : HuaweiMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker?): View {
        val mInfoView = (context as Activity).
        layoutInflater.inflate(R.layout.activity_custom_info_window, null)
        val mInfoWindow: InfoWindowData? = marker?.tag as InfoWindowData?
            mInfoView.infotitle.text = mInfoWindow?.sitename

            if (mInfoWindow?.sitephonenumber != null) {
                mInfoView.infophone.text = "Phone number:  " + mInfoWindow?.sitephonenumber
            } else {
                mInfoView.infophone.visibility = View.GONE
            }
            mInfoView.infoAddress.text = "Address: " + mInfoWindow?.siteadress

            val callnumber = mInfoView.findViewById<TextView>(R.id.infoGetDirections)

            callnumber.setOnClickListener() {
                val intent = Intent(Intent.ACTION_DIAL);
                intent.data = Uri.parse("tel:$" + mInfoWindow?.sitephonenumber)
                context.startActivity(intent)

            }
            val getdirections = mInfoView.findViewById<TextView>(R.id.GetDirections)
            getdirections.setOnClickListener() {
                val uriString =
                    "mapapp://navigation?saddr=" + mInfoWindow?.startlat + "," + mInfoWindow?.startlong + "&daddr=" + mInfoWindow?.endlat + "," + mInfoWindow?.endlong + "0&language=en&type=drive"
                val content_url = Uri.parse(uriString)
                val intent = Intent(Intent.ACTION_VIEW, content_url)
                context.startActivity(intent)


            }



            return mInfoView


    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }
    data class InfoWindowData(
        val sitename:String?,
        val sitephonenumber: String?,
        val siteadress: String?,
        val startlat:Double?,
        val startlong:Double?,

       val endlat:Double?,
       val endlong:Double?

    )

}