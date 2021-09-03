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

package com.team7.pregnancytracker.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.adapters.NotificationPermissionListAdapter
import com.team7.pregnancytracker.helpers.getRemoteConfigurations
import com.team7.pregnancytracker.models.entities.NotificationPermission
import com.team7.pregnancytracker.models.interfaces.IOnProgressListener
import com.team7.pregnancytracker.utils.Constants

class NotificationSettingsFragment : Fragment() {

    //Widgets
    private lateinit var progressView: View
    private lateinit var progressBar: ProgressBar
    private lateinit var noNotificationView: View
    private lateinit var notificationPermissionsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.registerReceiver(
            notificationPermissionListReceiver,
            IntentFilter(Constants.INTENT_ACTION_NOTIFICATION_PERMISSION_LIST_RECEIVED)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(notificationPermissionListReceiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notification_settings, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        progressView = view.findViewById(R.id.view_progress)
        progressBar = view.findViewById(R.id.progress_bar)
        noNotificationView = view.findViewById(R.id.view_no_notification)

        notificationPermissionsRecyclerView =
            view.findViewById(R.id.recycler_view_notification_permissions)

        getRemoteConfigurations(context!!)
        onProgress(true)
    }

    private val notificationPermissionListReceiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                onProgress(false)
                var notificationPermissionList = ArrayList<NotificationPermission>()
                if (intent.hasExtra(Constants.INTENT_PARAMETER_NOTIFICATION_PERMISSION_LIST)) {
                    notificationPermissionList =
                        intent.getSerializableExtra(Constants.INTENT_PARAMETER_NOTIFICATION_PERMISSION_LIST) as ArrayList<NotificationPermission>

                    if (notificationPermissionList.size > 0) {
                        noNotificationView.visibility = View.GONE
                        notificationPermissionsRecyclerView.visibility = View.VISIBLE
                    } else {
                        noNotificationView.visibility = View.VISIBLE
                        notificationPermissionsRecyclerView.visibility = View.GONE
                    }
                } else {
                    noNotificationView.visibility = View.VISIBLE
                    notificationPermissionsRecyclerView.visibility = View.GONE
                }
                prepareNotificationPermissionList(notificationPermissionList)
            }
        }

    private fun prepareNotificationPermissionList(notificationPermissionList: ArrayList<NotificationPermission>) {
        val notificationPermissionListAdapter =
            NotificationPermissionListAdapter(
                context!!,
                notificationPermissionList,
                object : IOnProgressListener {
                    override fun onProgress(isVisible: Boolean) {
                        this@NotificationSettingsFragment.onProgress(isVisible)
                    }
                })

        notificationPermissionsRecyclerView.itemAnimator = DefaultItemAnimator()
        notificationPermissionsRecyclerView.adapter = notificationPermissionListAdapter
        notificationPermissionsRecyclerView.requestLayout()
    }

    private fun onProgress(isVisible: Boolean) {
        progressView.visibility = (if (isVisible) View.VISIBLE else View.GONE)
        progressBar.visibility = (if (isVisible) View.VISIBLE else View.GONE)
    }
}