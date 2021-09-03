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

package com.team7.pregnancytracker.utils

import android.Manifest
import java.text.SimpleDateFormat
import java.util.*

class Constants {
    companion object {
        const val AGC_API_KEY =
            "CgB6e3x9nhUqZMpZ8E/T+bl+YzR3NvXOvovrpAMcHh2mMw0arupdLG4t9ajJc3DFQ/Ms2oSP+uMy/l4/MKRoZ7nH"

        val periodDateFormatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)


        const val ACTIVITY_REQUEST_CODE_HUAWEI_SIGN_IN = 53

        const val PERMISSION_REQUEST_CODE_CAMERA = 1

        const val IAP_KEY_ALGORITHMS = "SHA256WithRSA"
        const val IAP_PUBLIC_KEY = "MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAwpUfm0uaWOBsex+heAk4KfN03UhIoPUyscCxB9a8aFT6gkSccRlNvW2Ex0jwVRkRrc1U5iSIYwVNthofJLpWiN5ROOO4TQLScxR8SuJE1W9VGsY8FpgZLSGV/MfJ7zLtEm8TzJcq5JENzSefAikzt3PTH1avqaQ4dveyIzT2NzZVUwo9x0KmvkkbIH8HYJnwkvuaFuIvHrAxv4hFWtQ469S21YxAT2fVNF+GTmljBMYZcT9MTzz9tOHpuFBJB5gmGJ8XGznABCeBa/pB4lymyzV2wkm4PsJorgrnW3IBZGXnvoX+/s6/03jbccEzFhStv3or5lB/HQRytt3V4BWAHt/rIj/65JIEHstzpH1rvkfrBZkZcQkkBlm3xPIqycWx2+3aRGUoGgA6Ar7FHYTDtGn4j7n8ExrDUhusxTHoi3VPnySwPNRXS4NXOYOPpn/nqG9pXbS88OI+MmBiYGbTtM9NXnizoOPrYOaCejLIzykHi7cvA9EpDuV1P9TiH0A3AgMBAAE="

        const val IAP_PRODUCT_ID_3D_VIEW = "3DView"

        const val INTENT_PARAMETER_PROFILE = "intent_parameter_profile"
        const val INTENT_PARAMETER_MOOD = "intent_parameter_mood"
        const val INTENT_PARAMETER_EDITED_IMAGE = "intent_parameter_image"
        const val INTENT_PARAMETER_NOTIFICATION_PERMISSION_LIST = "intent_parameter_notification_permission_list"

        const val INTENT_ACTION_MOOD_RECEIVED = "intent_action_mood_received"
        const val INTENT_ACTION_IMAGE_RECEIVED = "intent_action_image_received"
        const val INTENT_ACTION_EDITED_IMAGE_RECEIVED = "intent_action_edited_image_received"
        const val INTENT_ACTION_NOTIFICATION_PERMISSION_LIST_RECEIVED = "intent_action_notification_permission_list_received"

        const val SHARED_PREFERENCES_KEY_USER_PROFILE = "user_profile"
        const val SHARED_PREFERENCES_KEY_LAST_PERIOD_DATE = "last_period_date"
        const val SHARED_PREFERENCES_KEY_ALLOWED_NOTIFICATIONS = "allowed_notifications"

        const val FRAGMENT_ARG_PARAM_DIARY = "diary"
        const val FRAGMENT_ARG_PARAM_PREGNANCY_INFO_TYPE = "pregnancy_info_type"
        const val FRAGMENT_ARG_PARAM_PREGNANCY_INFO = "pregnancy_info"
        const val FRAGMENT_ARG_PARAM_DIARY_TYPE = "diary_type"

        //External Db

        const val EXTERNAL_DB_NAME = "external_pregnancy.db"

        const val DB_TABLE_NAME_PREGNANCY_WEEKLY_INFO = "PregnancyWeeklyInfo"

        const val DB_COLUMN_NAME_INFO_ID = "id"
        const val DB_COLUMN_NAME_WEEK = "week"
        const val DB_COLUMN_NAME_BABY_INFO = "baby_info"
        const val DB_COLUMN_NAME_MOM_INFO = "mom_info"
        const val DB_COLUMN_NAME_BABY_SIZE = "baby_size"

        const val DB_NAME = "pregnancy.db"

        const val DB_TABLE_NAME_DIARY = "Diary"
        const val DB_TABLE_NAME_MEMORY = "Memory"
        const val DB_TABLE_NAME_CONTACT= "Contact"


        const val DB_COLUMN_NAME_TITLE = "Title"
        const val DB_COLUMN_NAME_CREATE_DATE = "CreateDate"
        const val DB_COLUMN_NAME_LAST_UPDATE_DATE = "LastUpdateDate"
        const val DB_COLUMN_NAME_TYPE = "Type"
        const val DB_COLUMN_NAME_MOOD = "Mood"
        const val DB_COLUMN_NAME_PHOTO = "Photo"
        const val DB_COLUMN_NAME_FILE_PATH = "FilePath"

        const val DB_COLUMN_NAME_ID = "Id"
        const val logTag = "EditImageFragment"

        const val REQUEST_PICK_IMAGE = 1001

        const val REMOTE_CONFIGURATION_KEY_NOTIFICATION_PERMISSION_LIST = "notification_permission_list"

        const val JSON_OBJECT_KEY_ID = "id"
        const val JSON_OBJECT_KEY_TOPIC = "topic"
        const val JSON_OBJECT_KEY_TOPIC_TEXT = "topic_text"

        const val ANALYTICS_EVENT_NAME_MOOD_TRACKER = "MoodTracker"
        const val ANALYTICS_EVENT_NAME_MOOD_DETECTOR = "MoodDetector"
        const val ANALYTICS_EVENT_NAME_DIARY_TRACKER = "DiaryTracker"
        const val ANALYTICS_EVENT_NAME_DATE_SETTER = "DateSetter"
        const val ANALYTICS_EVENT_NAME_BUTTON_TRACKER = "ButtonTracker"
        const val ANALYTICS_EVENT_NAME_MAP_TRACKER = "MapTracker"
        const val ANALYTICS_EVENT_NAME_IMAGE_FILTER_TRACKER = "ImageFilterTracker"
        const val ANALYTICS_EVENT_NAME_IMAGE_STICKER_TRACKER = "ImageStickerTracker"

        const val ANALYTICS_EVENT_BUNDLE_KEY_MOOD = "Mood"
        const val ANALYTICS_EVENT_BUNDLE_KEY_TYPE = "Type"
        const val ANALYTICS_EVENT_BUNDLE_KEY_BUTTON_NAME = "ButtonName"


        const val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE=1045

        const val root_url="https://siteapi.cloud.huawei.com/mapApi/v1/siteService/"


        val PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET

        )
        val filters: ArrayList<String> = arrayListOf(
            "No filter",
            "Black-and-white",
            "Brown tone",
            "Lazy",
            "Freesia",
            "Fuji",
            "Peach pink",
            "Sea salt",
            "Mint",
            "Reed",
            "Vintage",
            "Marshmallow",
            "Moss",
            "Sunlight",
            "Time",
            "Haze blue",
            "Sunflower",
            "Hard",
            "Bronze yellow",
            "Monochromic tone",
            "Yellow-green tone",
            "Yellow tone",
            "Green tone",
            "Cyan tone",
            "Violet tone"
        )
    }

}