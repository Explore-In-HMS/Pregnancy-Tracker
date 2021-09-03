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

import android.content.Context
import android.content.Intent
import android.util.Log
import com.huawei.agconnect.remoteconfig.AGConnectConfig
import com.team7.pregnancytracker.models.entities.NotificationPermission
import com.team7.pregnancytracker.utils.Constants
import org.json.JSONArray
import org.json.JSONException

private val TAG = "RemoteConfigurationHelper"

fun getRemoteConfigurations(context: Context) {
    Log.d(TAG, "Remote configuration request is being started.")
    val config = AGConnectConfig.getInstance()
    config.fetch().addOnSuccessListener {
        Log.d(TAG, "Remote configuration received successfully.")
        config.apply(it)
        val notificationPermissionListString: String =
            config.getValueAsString(Constants.REMOTE_CONFIGURATION_KEY_NOTIFICATION_PERMISSION_LIST)
        broadcastNotificationPermissionListReceived(
            context, parseNotificationPermissionList(
                context,
                notificationPermissionListString
            )
        )
    }.addOnFailureListener {
        Log.d(
            TAG,
            "Remote configuration fetch failed. ${it.message}"
        )
        broadcastNotificationPermissionListReceived(
            context, null
        )
    }
}

private fun broadcastNotificationPermissionListReceived(
    context: Context,
    notificationPermissionList: ArrayList<NotificationPermission>?
) {
    val intent = Intent()
    intent.action = Constants.INTENT_ACTION_NOTIFICATION_PERMISSION_LIST_RECEIVED
    if (notificationPermissionList != null) {
        intent.putExtra(
            Constants.INTENT_PARAMETER_NOTIFICATION_PERMISSION_LIST,
            notificationPermissionList
        )
    }
    context.sendBroadcast(intent)
}

private fun parseNotificationPermissionList(
    context: Context,
    notificationPermissionListString: String
): ArrayList<NotificationPermission> {
    val allowedNotifications = getFromSharedPreferences(
        context,
        Constants.SHARED_PREFERENCES_KEY_ALLOWED_NOTIFICATIONS,
        ""
    ) as String
    val notificationPermissionList: ArrayList<NotificationPermission> = ArrayList()
    try {
        val notificationPermissionListJSONArray =
            JSONArray(notificationPermissionListString)
        for (i in 0 until notificationPermissionListJSONArray.length()) {
            val notificationPermission =
                NotificationPermission(notificationPermissionListJSONArray.getJSONObject(i))
            notificationPermission.permission =
                allowedNotifications.contains(notificationPermission.topic)
            notificationPermissionList.add(notificationPermission)
        }
    } catch (e: JSONException) {
        Log.d(TAG, "JSON Exception. error => ${e.message}")
    }
    return notificationPermissionList
}
