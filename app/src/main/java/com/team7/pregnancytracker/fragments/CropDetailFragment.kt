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
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.huawei.hms.image.vision.crop.CropLayoutView
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.postButtonTrackerEvent
import com.team7.pregnancytracker.utils.Constants
import kotlinx.android.synthetic.main.fragment_crop_detail.*

class CropDetailFragment : Fragment(), View.OnClickListener{

    private var inputBm: Bitmap? = null
    private lateinit var cropImage: ImageButton
    private lateinit var flipH: ImageButton
    private lateinit var flipV: ImageButton
    private lateinit var rotate: ImageButton
    private lateinit var cropLayoutView: CropLayoutView
    private lateinit var rgCrop: RadioGroup
    private var rbCircular: RadioButton? = null
    private var rbRectangle: RadioButton? = null
    private lateinit var backbutton: ImageButton
    var bitmap: Bitmap? = null
    var selectedUri: Uri?=null

    private lateinit var okbutton:ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(arguments!!.getParcelable<Parcelable>("selectedImg")!=null) {
            bitmap = arguments!!.getParcelable<Parcelable>("selectedImg") as Bitmap
        }
        if(arguments!!.getParcelable<Parcelable>("pickedImg")!=null){
            selectedUri = arguments!!.getParcelable<Parcelable>("pickedImg") as Uri


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view= inflater.inflate(R.layout.fragment_crop_detail, container, false)
        init(view)
        return view
    }

    fun init(view: View) {
        val imgPhoto=view.findViewById<ImageView>(R.id.imgPhoto)
        okbutton=view.findViewById(R.id.okbttn)
        backbutton=view.findViewById(R.id.backbtn)
        cropLayoutView = view.findViewById(R.id.cropImageView)
        cropImage = view.findViewById(R.id.btn_crop_image)
        rotate = view.findViewById(R.id.btn_rotate)
        flipH = view.findViewById(R.id.btn_flip_horizontally)
        flipV = view.findViewById(R.id.btn_flip_vertically)
        cropLayoutView.setAutoZoomEnabled(true)
        cropLayoutView.setCropShape(CropLayoutView.CropShape.RECTANGLE)
        cropImage.setOnClickListener(this)
        rotate.setOnClickListener(this)
        flipH.setOnClickListener(this)
        flipV.setOnClickListener(this)
        rbCircular = view.findViewById(R.id.rb_circular)
        rgCrop = view.findViewById(R.id.rb_crop)
        if(bitmap!=null) {
            inputBm=bitmap
            imgPhoto.setImageBitmap(inputBm)
        }
        else{
            imgPhoto.setImageURI(selectedUri)
            inputBm = (imgPhoto!!.drawable as BitmapDrawable).bitmap

        }

        rgCrop.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton = radioGroup.findViewById<RadioButton>(i)
            if (radioButton == rbCircular) {
                postButtonTrackerEvent("Image Crop Oval")
                cropLayoutView.cropShape = CropLayoutView.CropShape.OVAL
            } else {
                postButtonTrackerEvent("Image Crop Rectangle")
                cropLayoutView.cropShape = CropLayoutView.CropShape.RECTANGLE
            }
        }



        rbRectangle = view.findViewById(R.id.rb_rectangle)
        okbutton.setOnClickListener(){
            val bitmap = cropLayoutView.croppedImage
            imgPhoto.visibility=View.VISIBLE
            imgPhoto?.setImageBitmap(inputBm)
            cropLayoutView.visibility = View.INVISIBLE
            val intent = Intent()
            intent.action = Constants.INTENT_ACTION_IMAGE_RECEIVED
            intent.putExtra(Constants.INTENT_PARAMETER_EDITED_IMAGE, bitmap)
            activity!!.sendBroadcast(intent)
            findNavController().navigateUp()


        }
        backbutton.setOnClickListener(){
            findNavController().navigateUp()
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_crop_image -> {
               postButtonTrackerEvent("Image Crop Select")
                imgPhoto.visibility=View.INVISIBLE
                cropLayoutView.visibility=View.VISIBLE
                cropLayoutView.setImageBitmap(inputBm)

            }
            R.id.btn_rotate-> {
                postButtonTrackerEvent("Image Rotate")
                val imageView3=view?.findViewById<ImageView>(R.id.imgPhoto)
                imageView3?.visibility=View.INVISIBLE
                cropLayoutView.setImageBitmap(inputBm)
                cropLayoutView.visibility = View.VISIBLE
                cropLayoutView.rotateClockwise()
            }
            R.id.btn_flip_horizontally-> {
                postButtonTrackerEvent("Image Flip Horizontally")
                val imageView3 = view?.findViewById<ImageView>(R.id.imgPhoto)
                imageView3?.visibility = View.INVISIBLE
                cropLayoutView.setImageBitmap(inputBm)
                cropLayoutView.visibility = View.VISIBLE
                cropLayoutView.flipImageHorizontally()
            }
            R.id.btn_flip_vertically-> {
                postButtonTrackerEvent("Image Flip Vertically")
                val imageView3=view?.findViewById<ImageView>(R.id.imgPhoto)
                imageView3?.visibility=View.INVISIBLE
                cropLayoutView.setImageBitmap(inputBm)
                cropLayoutView.visibility = View.VISIBLE
                cropLayoutView.flipImageVertically()
            }
        }
    }



}