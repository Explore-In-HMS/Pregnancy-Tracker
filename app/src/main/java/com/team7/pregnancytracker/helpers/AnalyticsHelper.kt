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

import android.os.Bundle
import android.util.Log
import com.team7.pregnancytracker.PregnancyApp
import com.team7.pregnancytracker.models.enums.EDiaryType
import com.team7.pregnancytracker.modules.moodTracker.EMood
import com.team7.pregnancytracker.utils.Constants

private const val TAG = "AnalyticsHelper"

fun postButtonTrackerEvent(buttonName: String) {
    Log.d(TAG, "Button track analytics event is being prepared.")
    val eventContentBundle = Bundle()
    eventContentBundle.putString(
        Constants.ANALYTICS_EVENT_BUNDLE_KEY_BUTTON_NAME,
        buttonName
    )
    postEventToAnalytics(
        Constants.ANALYTICS_EVENT_NAME_BUTTON_TRACKER,
        eventContentBundle
    )
}

fun postMoodTrackerEvent(mood: EMood) {
    Log.d(TAG, "Analytics event is being prepared.")
    val eventContentBundle = Bundle()
    eventContentBundle.putString(
        Constants.ANALYTICS_EVENT_BUNDLE_KEY_MOOD,
        mood.toString()
    )
    postEventToAnalytics(Constants.ANALYTICS_EVENT_NAME_MOOD_TRACKER, eventContentBundle)
}

fun postMoodDetectorEvent(mood: EMood) {
    Log.d(TAG, "Analytics event is being prepared.")
    val eventContentBundle = Bundle()
    eventContentBundle.putString(
        Constants.ANALYTICS_EVENT_BUNDLE_KEY_MOOD,
        mood.toString()
    )
    postEventToAnalytics(Constants.ANALYTICS_EVENT_NAME_MOOD_DETECTOR, eventContentBundle)
}

fun postDateSetterEvent(dateType: String) {
    Log.d(TAG, "Analytics event is being prepared.")
    val eventContentBundle = Bundle()
    eventContentBundle.putString(
        Constants.ANALYTICS_EVENT_BUNDLE_KEY_TYPE,
        dateType
    )
    postEventToAnalytics(
        Constants.ANALYTICS_EVENT_NAME_DATE_SETTER,
        eventContentBundle
    )
}

fun postDiaryTrackerEvent(diaryType: EDiaryType) {
    Log.d(TAG, "Analytics event is being prepared.")
    val eventContentBundle = Bundle()
    eventContentBundle.putString(
        Constants.ANALYTICS_EVENT_BUNDLE_KEY_TYPE,
        diaryType.toString()
    )
    postEventToAnalytics(
        Constants.ANALYTICS_EVENT_NAME_DIARY_TRACKER,
        eventContentBundle
    )
}

fun postMapTrackerEvent(type: String) {
    Log.d(TAG, "Analytics event is being prepared.")
    val eventContentBundle = Bundle()
    eventContentBundle.putString(
        Constants.ANALYTICS_EVENT_BUNDLE_KEY_TYPE,
        type
    )
    postEventToAnalytics(
        Constants.ANALYTICS_EVENT_NAME_MAP_TRACKER,
        eventContentBundle
    )
}

fun postImageFilterTrackerEvent(type: String) {
    Log.d(TAG, "Analytics event is being prepared.")
    val eventContentBundle = Bundle()
    eventContentBundle.putString(
        Constants.ANALYTICS_EVENT_BUNDLE_KEY_TYPE,
        type
    )
    postEventToAnalytics(
        Constants.ANALYTICS_EVENT_NAME_IMAGE_FILTER_TRACKER,
        eventContentBundle
    )
}

fun postImageStickerTrackerEvent(type: String) {
    Log.d(TAG, "Analytics event is being prepared.")
    val eventContentBundle = Bundle()
    eventContentBundle.putString(
        Constants.ANALYTICS_EVENT_BUNDLE_KEY_TYPE,
        type
    )
    postEventToAnalytics(
        Constants.ANALYTICS_EVENT_NAME_IMAGE_STICKER_TRACKER,
        eventContentBundle
    )
}

private fun postEventToAnalytics(eventName: String, eventContentBundle: Bundle) {
    Log.d(TAG, "Analytics event is being posted.")
    PregnancyApp.getAnalyticsInstance().onEvent(eventName, eventContentBundle)
}