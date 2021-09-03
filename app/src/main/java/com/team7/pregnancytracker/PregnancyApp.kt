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

package com.team7.pregnancytracker

import android.app.Application
import android.graphics.Typeface
import com.huawei.hms.analytics.HiAnalytics
import com.huawei.hms.analytics.HiAnalyticsInstance
import com.huawei.hms.analytics.HiAnalyticsTools

class PregnancyApp : Application() {

    private val TAG = PregnancyApp::class.java.simpleName
    private val PREGNANCY_APP_FONT_PATH = "fonts/pregnancy_app_font.ttf"

    companion object {
        private lateinit var pregnancyAppFont: Typeface
        fun getPregnancyAppFont(): Typeface {
            return pregnancyAppFont
        }

        private lateinit var analyticsInstance: HiAnalyticsInstance
        fun getAnalyticsInstance(): HiAnalyticsInstance {
            return analyticsInstance
        }
    }

    override fun onCreate() {
        super.onCreate()

        initTypeface()
        initAnalyticsInstance()
    }

    fun initTypeface() {
        pregnancyAppFont = Typeface.createFromAsset(assets, PREGNANCY_APP_FONT_PATH)
    }

    fun initAnalyticsInstance() {
        HiAnalyticsTools.enableLog()
        analyticsInstance = HiAnalytics.getInstance(this)
    }
}