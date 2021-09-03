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

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.huawei.hms.location.*
import com.huawei.hms.maps.*
import com.huawei.hms.maps.model.*
import com.huawei.hms.site.api.SearchResultListener
import com.huawei.hms.site.api.SearchService
import com.huawei.hms.site.api.SearchServiceFactory
import com.huawei.hms.site.api.model.*
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.activities.CustomInfoWindow
import com.team7.pregnancytracker.helpers.postMapTrackerEvent
import com.team7.pregnancytracker.models.entities.MarkerType
import com.team7.pregnancytracker.utils.Constants
import java.net.URLEncoder


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var hmap: HuaweiMap
    private lateinit var mMapView: MapView
    var mapViewBundle: Bundle? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private var MarkerArray = ArrayList<MarkerType>()
    private val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
    private lateinit var switchbutton: Switch



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        init(view, savedInstanceState)
        return view


    }



    private fun getLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)

        val mLocationCallback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                currentLatitude = locationResult.lastLocation.latitude
                currentLongitude = locationResult.lastLocation.longitude
                val build =
                    CameraPosition.Builder().target(LatLng(currentLatitude, currentLongitude))
                        .zoom(12f).build()
                val cameraUpdate = CameraUpdateFactory.newCameraPosition(build)
                hmap.animateCamera(cameraUpdate)
                hmap.setMaxZoomPreference(20f)
                hmap.setMinZoomPreference(1f)
                search()
            }
        }
        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1800000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationProviderClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.getMainLooper()
        ).addOnSuccessListener {}.addOnFailureListener {}


    }

    fun search() {

        val searchService: SearchService
        searchService = SearchServiceFactory.create(
            activity,
            URLEncoder.encode(
                "CgB6e3x93nA+atqUFALpXpv37MBVbUKLc9k2cLHgoHVAEK/dhKwGBaX2+8FaUMjaGHoUoP0Lj7BavCpUErFBxqho",
                "utf-8"
            )
        )
        val request = NearbySearchRequest()
        val location = Coordinate(currentLatitude, currentLongitude)
        request.location = location
        if (switchbutton.isChecked) {
            request.query = "HOSPITAL"
            request.poiType = LocationType.HOSPITAL
            request.radius = 10000
        } else {
            request.query = "PHARMACY"
            request.poiType = LocationType.PHARMACY
            request.radius = 2000

        }
        request.language = "tr"
        request.pageIndex = 1
        request.pageSize = 10


        val resultListener: SearchResultListener<NearbySearchResponse?> =
            object : SearchResultListener<NearbySearchResponse?> {
                override fun onSearchResult(results: NearbySearchResponse?) {
                    val sites = results!!.sites
                    if (results == null || results.totalCount <= 0 || sites == null || sites.size <= 0) {
                        return
                    }
                    val i: Int = 0
                    for (site in sites) {
                        val a: MarkerType
                        if (site.name != null || site.poi.phone != null || site.formatAddress != null) {
                            a = MarkerType(
                                site.location.lat,
                                site.location.lng,
                                site.poi.poiTypes[i],
                                site.name,
                                site.poi.phone,
                                site.formatAddress
                            )
                            MarkerArray.add(a)

                        }
                        Log.i(
                            "TAG",
                            String.format(
                                "siteId: '%s', name: %s\r\n",
                                site.siteId,
                                site.name


                            )
                        )
                    }
                    addMarker(MarkerArray)

                }

                override fun onSearchError(status: SearchStatus) {
                    Log.i(
                        "TAG",
                        "Error : " + status.errorCode + " " + status.errorMessage
                    )
                }
            }
        searchService.nearbySearch(request, resultListener)
    }

   private fun addMarker(mArray: ArrayList<MarkerType>) {
        hmap.clear()

        val mArraylist = mArray
        for (m in mArraylist) {
            if (m.poitype.equals("HOSPITAL")) {

                val options = MarkerOptions()
                    .position(LatLng(m.lat, m.lang))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital_new))
                    .clusterable(true)
                val info = CustomInfoWindow.InfoWindowData(
                    m.sitename,
                    m.sitephonenumber,
                    m.siteadress,
                    currentLatitude,
                    currentLongitude,
                    m.lat,
                    m.lang
                )
                hmap.setMarkersClustering(true)
                val marker = hmap.addMarker(options)

                val customInfoWindow = context?.let { ctx -> CustomInfoWindow(ctx) }
                marker.tag = info
                hmap.setInfoWindowAdapter(customInfoWindow)



                hmap.setOnMarkerClickListener { marker ->
                    val clusterable = marker.isClusterable
                    if (clusterable == false) {
                        hmap.clear()
                        search()
                        val cameraUpdate = CameraUpdateFactory.zoomBy(1f)
                        hmap.moveCamera(cameraUpdate)


                    }
                    false
                }


            }

            if (m.poitype.equals("DRUGSTORE")) {

                val options = MarkerOptions()
                    .position(LatLng(m.lat, m.lang))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_tablets_and_pill))
                    .clusterable(true)
                val info = CustomInfoWindow.InfoWindowData(
                    m.sitename,
                    m.sitephonenumber,
                    m.siteadress,
                    currentLatitude,
                    currentLongitude,
                    m.lat,
                    m.lang
                )
                hmap.setMarkersClustering(true);
                val marker = hmap.addMarker(options)
                val customInfoWindow = context?.let { ctx -> CustomInfoWindow(ctx) }
                marker.tag = info
                hmap.setInfoWindowAdapter(customInfoWindow)
                hmap.setOnMarkerClickListener { marker ->
                    val clusterable = marker.isClusterable
                    if (clusterable == false) {
                        hmap.clear()
                        search()
                        val cameraUpdate = CameraUpdateFactory.zoomBy(1f)
                        hmap.moveCamera(cameraUpdate)
                    }
                    false
                }


            }
        }
        MarkerArray.clear()


    }



    fun init(view: View, savedInstanceState: Bundle?) {

        val text = view.findViewById<TextView>(R.id.textphrhos)
        switchbutton = view.findViewById<Switch>(R.id.swith_hosp_pharmcy)

            switchbutton.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked) {
                    switchbutton.setText("HOSPITAL")
                    text.setText("Switch for pharmacies")
                    getLocation()

                    postMapTrackerEvent("Hospital")
                } else {
                    switchbutton.setText("PHARMACY")
                    text.setText("Switch for hospitals")
                    getLocation()

                    postMapTrackerEvent("Pharmacy")
                }
            }

        mMapView = view.findViewById(R.id.mapView)
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        MapsInitializer.setApiKey(Constants.AGC_API_KEY)
        mMapView.apply {
            onCreate(mapViewBundle)
            getMapAsync(this@MapFragment)

        }
        getLocation()
    }

    override fun onMapReady(map: HuaweiMap?) {
        if (map != null) {
            hmap = map
            hmap.isMyLocationEnabled = true

        }


    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()

    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onDestroyView() {
        try {
            val fragment = activity!!.supportFragmentManager.findFragmentById(R.id.mapView)
            val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
            if (fragment != null) {
                ft.remove(fragment)
            }
            ft.commit()
        } catch (e: Exception) {
            Log.i(
                "TAG",
                "Could not destroy"
            )
        }
        super.onDestroyView()

    }


    override fun onPause() {
        mMapView.onPause()
        super.onPause()

    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }


}