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

package com.team7.pregnancytracker.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.push.HmsMessaging
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.getFromSharedPreferences
import com.team7.pregnancytracker.helpers.putToSharedPreferences
import com.team7.pregnancytracker.models.entities.NotificationPermission
import com.team7.pregnancytracker.models.interfaces.IOnProgressListener
import com.team7.pregnancytracker.utils.Constants
import com.team7.pregnancytracker.widgets.iconSwitch.IconSwitch

class NotificationPermissionListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Class Variables
    private var context: Context
    private var notificationPermissionList: ArrayList<NotificationPermission>
    private var onProgressListener: IOnProgressListener

    constructor(
        context: Context,
        notificationPermissionList: ArrayList<NotificationPermission>,
        onProgressListener: IOnProgressListener
    ) {
        this.context = context
        this.notificationPermissionList = notificationPermissionList
        this.onProgressListener = onProgressListener
    }

    class NotificationPermissionViewHolder : RecyclerView.ViewHolder {
        var topicTextView: TextView
        var permissionStatusSwitch: IconSwitch

        constructor(itemView: View) : super(itemView) {
            topicTextView = itemView.findViewById(R.id.text_view_topic)
            permissionStatusSwitch = itemView.findViewById(R.id.switch_permission_status)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_template_notification_permission, parent, false)
        return NotificationPermissionViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return notificationPermissionList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val notificationPermissionViewHolder = holder as NotificationPermissionViewHolder

        val notificationPermission = notificationPermissionList.get(position)
        notificationPermissionViewHolder.topicTextView.text = notificationPermission.topicText
        var checked = IconSwitch.Checked.LEFT
        if (notificationPermission.permission) {
            checked = IconSwitch.Checked.RIGHT
        }
        notificationPermissionViewHolder.permissionStatusSwitch.checked = checked
        notificationPermissionViewHolder.permissionStatusSwitch.setCheckedChangeListener {
            if (it == IconSwitch.Checked.LEFT) {
                //Notification Off
                unsubscribeTopic(
                    notificationPermissionViewHolder.permissionStatusSwitch,
                    notificationPermission
                )
            } else {
                //Notification On
                subscribeTopic(
                    notificationPermissionViewHolder.permissionStatusSwitch,
                    notificationPermission
                )
            }
        }
    }

    private fun subscribeTopic(
        notificationPermissionSwitch: IconSwitch,
        notificationPermission: NotificationPermission
    ) {
        onProgressListener.onProgress(true)
        HmsMessaging.getInstance(context).subscribe(notificationPermission.topic)
            .addOnCompleteListener {
                onProgressListener.onProgress(false)
                if (it.isSuccessful) {
                    Toast.makeText(
                        context,
                        R.string.text_toast_operation_successful,
                        Toast.LENGTH_LONG
                    )
                        .show()
                    addToAllowedNotifications(notificationPermission)
                } else {
                    Toast.makeText(context, R.string.text_toast_unexpected_error, Toast.LENGTH_LONG)
                        .show()
                    notificationPermissionSwitch.setCheckedChangeListener(null)
                    notificationPermissionSwitch.checked = IconSwitch.Checked.LEFT
                    notificationPermissionSwitch.setCheckedChangeListener {
                        if (it == IconSwitch.Checked.LEFT) {
                            //Notification Off
                            unsubscribeTopic(
                                notificationPermissionSwitch,
                                notificationPermission
                            )
                        } else {
                            //Notification On
                            subscribeTopic(
                                notificationPermissionSwitch,
                                notificationPermission
                            )
                        }
                    }
                }
            }
    }

    private fun unsubscribeTopic(
        notificationPermissionSwitch: IconSwitch,
        notificationPermission: NotificationPermission
    ) {
        onProgressListener.onProgress(true)
        HmsMessaging.getInstance(context).unsubscribe(notificationPermission.topic)
            .addOnCompleteListener {
                onProgressListener.onProgress(false)
                if (it.isSuccessful) {
                    Toast.makeText(
                        context,
                        R.string.text_toast_operation_successful,
                        Toast.LENGTH_LONG
                    )
                        .show()
                    removeFromAllowedNotifications(notificationPermission)
                } else {
                    Toast.makeText(context, R.string.text_toast_unexpected_error, Toast.LENGTH_LONG)
                        .show()
                    notificationPermissionSwitch.setCheckedChangeListener(null)
                    notificationPermissionSwitch.checked = IconSwitch.Checked.RIGHT
                    notificationPermissionSwitch.setCheckedChangeListener {
                        if (it == IconSwitch.Checked.LEFT) {
                            //Notification Off
                            unsubscribeTopic(
                                notificationPermissionSwitch,
                                notificationPermission
                            )
                        } else {
                            //Notification On
                            subscribeTopic(
                                notificationPermissionSwitch,
                                notificationPermission
                            )
                        }
                    }
                }
            }
    }

    private fun addToAllowedNotifications(notificationPermission: NotificationPermission) {
        var allowedNotifications = getFromSharedPreferences(
            context,
            Constants.SHARED_PREFERENCES_KEY_ALLOWED_NOTIFICATIONS,
            ""
        ) as String
        allowedNotifications += ",${notificationPermission.topic}"
        putToSharedPreferences(
            context,
            Constants.SHARED_PREFERENCES_KEY_ALLOWED_NOTIFICATIONS,
            allowedNotifications
        )
    }

    private fun removeFromAllowedNotifications(notificationPermission: NotificationPermission) {
        var allowedNotifications = getFromSharedPreferences(
            context,
            Constants.SHARED_PREFERENCES_KEY_ALLOWED_NOTIFICATIONS,
            ""
        ) as String
        allowedNotifications = allowedNotifications.replace(",${notificationPermission.topic}", "")
        putToSharedPreferences(
            context,
            Constants.SHARED_PREFERENCES_KEY_ALLOWED_NOTIFICATIONS,
            allowedNotifications
        )
    }
}