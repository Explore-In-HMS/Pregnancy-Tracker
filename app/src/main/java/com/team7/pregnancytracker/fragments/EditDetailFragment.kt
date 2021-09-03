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
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.postButtonTrackerEvent
import com.team7.pregnancytracker.utils.Constants

var bitmap: Bitmap? = null
var selectedUri:Uri?=null
private  lateinit var btnfilter: ImageButton
private  lateinit var btncrop: ImageButton
private  lateinit var btnSticker: ImageButton
private lateinit var editedimgview:ImageView
private lateinit var okbuttonn:ImageButton
private lateinit var backbuttonn:ImageButton
class EditDetailFragment : Fragment()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(arguments!!.getParcelable<Parcelable>("selectedImg")!=null) {
            bitmap = arguments!!.getParcelable<Parcelable>("selectedImg") as Bitmap
        }
        if(arguments!!.getParcelable<Parcelable>("pickedImg")!=null){
            selectedUri = arguments!!.getParcelable<Parcelable>("pickedImg") as Uri

        }
        activity!!.registerReceiver(
            ImageReceiver,
            IntentFilter(Constants.INTENT_ACTION_IMAGE_RECEIVED)
        )
    }
    override fun onDestroy() {
        super.onDestroy()
        activity!!.unregisterReceiver(ImageReceiver)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_edit_detail, container, false)
        init(view)
        return view
    }
    fun init(view: View){
        btnfilter=view.findViewById(R.id.btn_filter)
        btncrop=view.findViewById(R.id.btn_crop)
        btnSticker=view.findViewById(R.id.btn_sticker)
        okbuttonn=view.findViewById(R.id.okbttn)
        backbuttonn=view.findViewById(R.id.backbtn)

        editedimgview=view.findViewById<ImageView>(R.id.imgPhoto)
        if(bitmap!=null) {
            editedimgview.setImageBitmap(bitmap)
        }
        else{
            editedimgview.setImageURI(selectedUri)

        }
        btnSticker.setOnClickListener{
            val bundle = Bundle()
            bitmap = (editedimgview.drawable as BitmapDrawable).bitmap
            bundle.putParcelable("selectedImg", bitmap)

            postButtonTrackerEvent("Image Sticker")
           findNavController().navigate(
                R.id.action_navigation_image_edit_to_navigation_image_sticker,
                bundle
            )
        }
        btnfilter.setOnClickListener {
            val bundle = Bundle()
            bitmap = (editedimgview.drawable as BitmapDrawable).bitmap
            bundle.putParcelable("selectedImg", bitmap)

          postButtonTrackerEvent("Image Filter")
            findNavController().navigate(
                R.id.action_navigation_image_edit_to_navigation_image_filter,
                bundle
            )
        }
        btncrop.setOnClickListener{
            val bundle = Bundle()
            bitmap = (editedimgview.drawable as BitmapDrawable).bitmap
            bundle.putParcelable("selectedImg", bitmap)

            postButtonTrackerEvent("Image Crop")
            findNavController().navigate(
                R.id.action_navigation_image_edit_to_navigation_image_crop,
                bundle
            )
        }
        okbuttonn.setOnClickListener(){
            bitmap = (editedimgview.drawable as BitmapDrawable).bitmap

            val intent = Intent()
            intent.action = Constants.INTENT_ACTION_EDITED_IMAGE_RECEIVED
            intent.putExtra(Constants.INTENT_PARAMETER_EDITED_IMAGE, bitmap)
            activity!!.sendBroadcast(intent)
            findNavController().navigateUp()



        }
        backbuttonn.setOnClickListener(){
            findNavController().navigateUp()
        }
    }

    private val ImageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val editedimage = intent.getParcelableExtra(Constants.INTENT_PARAMETER_EDITED_IMAGE) as Bitmap

            editedimgview.setImageBitmap(editedimage)
        }
    }
}
