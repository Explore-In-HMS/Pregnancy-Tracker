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

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.huawei.hmf.tasks.OnFailureListener
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.location.*
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.adapters.ContactListAdapter
import com.team7.pregnancytracker.helpers.DatabaseHelper
import com.team7.pregnancytracker.helpers.RetrofitClient
import com.team7.pregnancytracker.models.entities.Contact
import com.team7.pregnancytracker.models.entities.GeocodingLocation
import com.team7.pregnancytracker.models.entities.GeocodingResponse
import com.team7.pregnancytracker.models.entities.SiteGeo
import com.team7.pregnancytracker.models.interfaces.IOnItemSelectListener
import com.team7.pregnancytracker.models.interfaces.PostService
import com.team7.pregnancytracker.utils.Constants
import kotlin.collections.ArrayList


class ContactFragment : Fragment() {

    private lateinit var contactRecyclerView: RecyclerView
    private lateinit var noContactView: View
    private lateinit var addContactFab: ExtendedFloatingActionButton
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private lateinit var sendnot: ImageButton
    private lateinit var progress: ProgressBar


    private var isAddMenuVisible = false
    private var menuSize: Float = 0f


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        progress = view.findViewById(R.id.progressBar2)
        progress.isIndeterminate = false
        progress.visibility = View.INVISIBLE
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        locationStart()

        menuSize =
            (resources.getDimension(R.dimen.size_large) + resources.getDimension(R.dimen.margin_container_small)) * -1


        if (!hasPermissions(activity, *Constants.PERMISSIONS)) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    Constants.PERMISSIONS,
                    1
                )
            }

        }
        noContactView = view.findViewById(R.id.view_no_contact)
        val contactList =
            DatabaseHelper.getDatabaseHelper(activity!!)!!.contactDao().getAllContacts()
        contactRecyclerView = view.findViewById(R.id.recycler_view_contact)
        addContactFab = view.findViewById(R.id.fab_add_contact)
        sendnot = view.findViewById(R.id.sendnot)
        val contactListAdapter = ContactListAdapter(contactList, object : IOnItemSelectListener {
            override fun onItemSelect(item: Any, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable(
                    ContactDetailFragment.FRAGMENT_ARG_PARAM_CONTACT,
                    item as Contact
                )
                findNavController().navigate(
                    R.id.action_navigation_contact_to_navigation_contact_info_detail,
                    bundle
                )
            }

            override fun onbuttonclick(item: Any, position: Int) {
                progress.visibility = View.VISIBLE

                val list: ArrayList<String> = ArrayList<String>()
                list.add(item.toString())
                sites(currentLatitude, currentLongitude, list)

            }


        })

        val layoutManager = LinearLayoutManager(activity)
        contactRecyclerView.layoutManager = layoutManager
        contactRecyclerView.itemAnimator = DefaultItemAnimator()
        contactRecyclerView.adapter = contactListAdapter

        if (contactList.isEmpty()) {
            noContactView.visibility = View.VISIBLE
            contactRecyclerView.visibility = View.GONE
        } else {
            noContactView.visibility = View.GONE
            contactRecyclerView.visibility = View.VISIBLE
        }


        addContactFab.setOnClickListener {
            hideAddMenu()
            findNavController().navigate(R.id.action_navigation_contact_to_navigation_contact_detail)
        }

        sendnot.setOnClickListener {
            progress.visibility = View.VISIBLE

            val contactList =
                DatabaseHelper.getDatabaseHelper(activity!!)!!.contactDao().getAllContacts()

            val list: ArrayList<String> = ArrayList()
            for (contacts in contactList) {

                list.add(contacts.number)


            }
            sites(currentLatitude, currentLongitude, list)

        }

    }

    private fun hideAddMenu() {
        addContactFab.shrink()
        isAddMenuVisible = false
    }

    private fun hasPermissions(activity: FragmentActivity?, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (activity?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        permission

                    )
                }

                != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }



        return true
    }


    private fun locationStart() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 10000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val mLocationCallback: LocationCallback
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null) {
                    currentLatitude = locationResult.lastLocation.latitude
                    currentLongitude = locationResult.lastLocation.longitude
                }
            }
        }
        fusedLocationProviderClient
            ?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
            ?.addOnSuccessListener(object : OnSuccessListener<Void?> {
                override fun onSuccess(aVoid: Void?) {}
            })
            ?.addOnFailureListener(object : OnFailureListener {
                override fun onFailure(e: Exception?) {}
            })
    }


    private fun sites(
        currentlattitude: Double,
        currentLongtitude: Double,
        number: ArrayList<String>
    ) {

        val postService = RetrofitClient.getClient().create(PostService::class.java)
        val location = GeocodingResponse()
        location.location = GeocodingLocation()
        location.location!!.lng = currentLongitude
        location.location!!.lat = currentLatitude
        val post = postService.listPost(location)
        post.enqueue(object : retrofit2.Callback<SiteGeo> {
            override fun onFailure(call: retrofit2.Call<SiteGeo>, t: Throwable) {
                Toast.makeText(context, t.message.toString(), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: retrofit2.Call<SiteGeo>,
                response: retrofit2.Response<SiteGeo>
            ) {

                if (response.isSuccessful) {
                    val adres = response.body()!!.sites?.get(1)?.formatAddress

                    val uri = StringBuilder("sms:")
                    for (i in 0 until number.size) {
                        uri.append(number[i])
                        uri.append(", ")
                    }
                    val smsIntent = Intent(Intent.ACTION_VIEW)
                    smsIntent.type = "vnd.android-dir/mms-sms"
                    smsIntent.data = Uri.parse(uri.toString())
                    smsIntent.putExtra("sms_body", adres)
                    startActivity(smsIntent)
                    progress.visibility = View.INVISIBLE

                }

            }


        })
    }


}

