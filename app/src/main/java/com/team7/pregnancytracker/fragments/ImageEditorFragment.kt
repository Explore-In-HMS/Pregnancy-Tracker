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

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.DatabaseHelper
import com.team7.pregnancytracker.helpers.postButtonTrackerEvent
import com.team7.pregnancytracker.models.entities.Memory
import com.team7.pregnancytracker.utils.Constants
import java.io.ByteArrayOutputStream
import java.util.*

class ImageEditorFragment : Fragment() {
    private lateinit var imageview: ImageView
    private lateinit var popupButton: TextView
    private lateinit var popupMenu: PopupMenu
    private lateinit var editbutton: Button
    private lateinit var savebutton: Button
    private lateinit var memorytitle: EditText
    private lateinit var backbutton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity!!.registerReceiver(
            imageReceiver,
            IntentFilter(Constants.INTENT_ACTION_EDITED_IMAGE_RECEIVED)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(imageReceiver)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_editor, container, false)
        editbutton = view.findViewById(R.id.button_edit)
        savebutton = view.findViewById(R.id.button_save)
        popupButton = view.findViewById<TextView>(R.id.text_view_add_memory)
        imageview = view.findViewById(R.id.image_view_photo)
        memorytitle = view.findViewById(R.id.edit_text_memory_title)
        backbutton = view.findViewById(R.id.image_button_back)
        popupMenu = PopupMenu(activity, popupButton)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { MenuItem ->
            when (MenuItem.itemId) {
                R.id.first -> {
                    checkCameraPermission()
                }
                R.id.second -> {
                    pickGallery()
                }
            }

            false
        }

        popupButton.setOnClickListener {
            popupMenu.show()
        }
        editbutton.setOnClickListener {
            postButtonTrackerEvent("Image Edit")
            passImage()
        }
        savebutton.setOnClickListener {
            val memorytitletext = memorytitle.text.toString()
            val currentTime = Calendar.getInstance().timeInMillis
            var bitmap = (imageview.drawable as BitmapDrawable).bitmap
            bitmap = Bitmap.createScaledBitmap(bitmap, 360, 360, true)

            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            val image = stream.toByteArray()
            val memory = Memory(
                memorytitletext,
                "Image",
                image,
                currentTime

            )
            postButtonTrackerEvent("Image Add")
            DatabaseHelper.getDatabaseHelper(context!!)!!.memoryDao().insert(memory)
            Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }
        backbutton.setOnClickListener {
            findNavController().navigateUp()

        }
        return view

    }


    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                Constants.PERMISSION_REQUEST_CODE_CAMERA
            )
        } else {
            takePhoto()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.PERMISSION_REQUEST_CODE_CAMERA -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {

                    takePhoto()
                }
            }
        }
    }

    private fun takePhoto() {

        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (activity?.packageManager?.let { it1 -> callCameraIntent.resolveActivity(it1) } != null) {
            startActivityForResult(callCameraIntent, Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        }
    }

    private fun pickGallery() {
        val getPhotoIntent = Intent(Intent.ACTION_GET_CONTENT)
        val mimeTypes = arrayOf("image/jpg", "image/png", "image/jpeg")
        getPhotoIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        getPhotoIntent.type = "image/*"
        getPhotoIntent.addCategory(Intent.CATEGORY_OPENABLE)
        this.startActivityForResult(getPhotoIntent, Constants.REQUEST_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (null != intent) {
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    Constants.REQUEST_PICK_IMAGE -> try {

                        val uri: Uri? = intent.data
                        imageview.setImageURI(uri)
                        editbutton.visibility = View.VISIBLE

                    } catch (e: Exception) {
                        System.out.println(e)
                    }
                }
            }
            if (resultCode == Activity.RESULT_OK && requestCode == Constants.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {

                val Bitmaps = (intent.extras?.get("data") as Bitmap)
                imageview.setImageBitmap(Bitmaps)
                editbutton.visibility = View.VISIBLE


            }
        }
    }

    private fun passImage() {
        val bundle = Bundle()
        bitmap = (imageview.drawable as BitmapDrawable).bitmap
        val scaledbitmap = Bitmap.createScaledBitmap(bitmap!!, 360, 360, true)
        bundle.putParcelable("selectedImg", scaledbitmap)
        findNavController().navigate(
            R.id.action_navigation_image_details_to_navigation_image_edit,
            bundle
        )

    }

    private val imageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val editedimage =
                intent.getParcelableExtra(Constants.INTENT_PARAMETER_EDITED_IMAGE) as Bitmap

            imageview.setImageBitmap(editedimage)
        }
    }


}

