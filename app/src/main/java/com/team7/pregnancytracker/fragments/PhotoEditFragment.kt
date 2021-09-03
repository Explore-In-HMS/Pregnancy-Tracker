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

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.image.vision.ImageVision
import com.huawei.hms.image.vision.ImageVisionImpl
import com.team7.pregnancytracker.R

import com.team7.pregnancytracker.helpers.CustomRVAdapter
import com.team7.pregnancytracker.helpers.postImageFilterTrackerEvent
import com.team7.pregnancytracker.utils.Constants
import com.team7.pregnancytracker.utils.Strings
import org.json.JSONException
import org.json.JSONObject

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class PhotoEditFragment : Fragment(), CustomRVAdapter.OnImgClickListener {
    lateinit var imageVisionAPI: ImageVisionImpl
    var authJson: JSONObject? = null
    var projectDetailsString =
        "{\"projectId\":\"projectIdTest\",\"appId\":\"appIdTest\",\"authApiKey\":\"authApiKeyTest\",\"clientSecret\":\"clientSecretTest\",\"clientId\":\"clientIdTest\",\"token\":\"tokenTest\"}"
    var initCode = -1
    private lateinit var okbutton: ImageButton
    private lateinit var backbutton: ImageButton

    var rcyc: RecyclerView? = null
    var imgview: ImageView? = null
    var filterNameText: TextView? = null
    var bitmap: Bitmap? = null
    var selectedUri: Uri? = null
    private lateinit var adapter: CustomRVAdapter

    private var filteredImageList: ArrayList<Drawable> = ArrayList()

    var executorService: ExecutorService = Executors.newFixedThreadPool(1)
    var isSubFilterDone = false
    var filteredImgName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments!!.getParcelable<Parcelable>("selectedImg") != null) {
            bitmap = arguments!!.getParcelable<Parcelable>("selectedImg") as Bitmap
        }
        if (arguments!!.getParcelable<Parcelable>("pickedImg") != null) {
            selectedUri = arguments!!.getParcelable<Parcelable>("pickedImg") as Uri

        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_edit, container, false)
        rcyc = view.findViewById(R.id.rvList)
        imgview = view.findViewById(R.id.loadedImageView)
        filterNameText = view.findViewById(R.id.filterName)
        okbutton = view.findViewById(R.id.okbttn)
        backbutton = view.findViewById(R.id.backbtn)

        init()
        return view
    }

    fun init() {
        if (bitmap != null) {
            imgview!!.setImageBitmap(bitmap)
            for (i in 0..24) {
                filteredImageList.add(imgview!!.drawable)
            }
            initAuthJson()
            initImageVisionAPI(activity)
            setRecyclerView()
            if (initCode != -1) {
                startFilterForRecyclerView("1", "1", authJson)
                rcyc?.scrollToPosition(23)
            }
        } else if (selectedUri != null) {

            imgview?.setImageURI(selectedUri)
            bitmap = (imgview!!.drawable as BitmapDrawable).bitmap
            for (i in 0..24) {
                filteredImageList.add(imgview!!.drawable)
            }
            initAuthJson()
            initImageVisionAPI(activity)
            setRecyclerView()
            if (initCode != -1) {
                startFilterForRecyclerView("1", "1", authJson)
                rcyc?.scrollToPosition(23)
            }
        }
        okbutton.setOnClickListener() {
            bitmap = (imgview!!.drawable as BitmapDrawable).bitmap

            val intent = Intent()
            intent.action = Constants.INTENT_ACTION_IMAGE_RECEIVED
            intent.putExtra(Constants.INTENT_PARAMETER_EDITED_IMAGE, bitmap)
            activity!!.sendBroadcast(intent)
            findNavController().navigateUp()


        }
        backbutton.setOnClickListener() {
            findNavController().navigateUp()
        }
    }

    private fun setRecyclerView() {
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        adapter = CustomRVAdapter(Constants.filters, filteredImageList, this)

        if (rcyc != null) {
            rcyc!!.setHasFixedSize(true)
            rcyc!!.layoutManager = layoutManager

            rcyc!!.adapter = adapter

        }
    }

    private fun initAuthJson() {
        try {
            authJson = JSONObject(projectDetailsString)
        } catch (e: JSONException) {
            Log.i(Constants.logTag, e.toString())
        }
    }

    private fun initImageVisionAPI(context: Context?) {
        imageVisionAPI = ImageVision.getInstance(context)
        imageVisionAPI.setVisionCallBack(object : ImageVision.VisionCallBack {
            override fun onSuccess(successCode: Int) {
                initCode = imageVisionAPI.init(context, authJson)
            }

            override fun onFailure(errorCode: Int) {

            }
        })
    }

    private fun stopFilterImageVisionApi() {
        imageVisionAPI.stop()
    }

    private fun startFilterForRecyclerView(
        intensity: String,
        compress: String,
        authJson: JSONObject?
    ) {
        val runnable = Runnable {
            Log.w(Constants.logTag, "Inside runnable")
            initImageVisionAPI(context)
            filteredImageList.clear()
            val jsonObject = JSONObject()
            val taskJson = JSONObject()
            try {
                for (i in 0..24) {
                    taskJson.put(Strings.intensity, intensity)
                    taskJson.put(Strings.filterType, i)
                    taskJson.put(Strings.compressRate, compress)
                    jsonObject.put(Strings.requestId, 1)
                    jsonObject.put(Strings.taskJson, taskJson)
                    jsonObject.put(Strings.authJson, authJson)
                    val visionResult = imageVisionAPI.getColorFilter(
                        jsonObject,
                        bitmap
                    )
                    val image = visionResult.image
                    val d = BitmapDrawable(resources, image)
                    filteredImageList.add(d)
                }
                stopFilterImageVisionApi()
                isSubFilterDone = true
                scrollOnUI(0)
            } catch (e: JSONException) {
                Log.i(Constants.logTag, e.toString())
            }
        }
        executorService.execute(runnable)
    }

    override fun onImgClick(position: Int) {
        super.onImgClick(position)
        postImageFilterTrackerEvent(Constants.filters[position])
        filterNameText!!.text = Constants.filters[position]
        imgview!!.setImageBitmap(filteredImageList[position].toBitmap())
        filteredImgName = Constants.filters[position]
    }


    private fun scrollOnUI(pos: Int) {
        runOnUiThread {
            rcyc?.scrollToPosition(pos)
        }


    }

    fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }

}
