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
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hms.image.vision.sample.Utility
import com.huawei.hms.image.vision.sticker.StickerLayout
import com.huawei.hms.image.vision.sticker.item.TextEditInfo
import com.huawei.secure.android.common.util.LogsUtil
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.CustomRVAdapter
import com.team7.pregnancytracker.helpers.postImageStickerTrackerEvent
import com.team7.pregnancytracker.utils.Constants
import java.io.File


class StickerDetailFragment : Fragment(), CustomRVAdapter.OnImgClickListener {
    private var btn_removeSticks: Button? = null
    private var fonts: EditText? = null
    private var mStickerLayout: StickerLayout? = null
    private var textEditInfo: TextEditInfo? = null
    private lateinit var iv: ImageView
    var rootPath = ""
    private lateinit var adapter: CustomRVAdapter
    var rcyc: RecyclerView? = null
    private lateinit var okbtton: ImageButton
    private lateinit var backbtton: ImageButton
    val PERMISSION_REQUEST_CODE = 0x01
    var bitmap: Bitmap? = null
    var selectedUri: Uri? = null
    var result: Int? = null
    private var filteredImageList: ArrayList<Drawable> = ArrayList()
    private var stikernames: ArrayList<String> = ArrayList()
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
        val view = inflater.inflate(R.layout.fragment_sticker_detail, container, false)
        init(view)
        return view
    }

    fun init(view: View) {
        try {
            rootPath = activity?.baseContext?.filesDir?.path + "/vgmap/"
            initData()
        } catch (e: Exception) {
            LogsUtil.e(TAG, "Exception: " + e.message)
        }
        rcyc = view.findViewById(R.id.rvListSticker)
        okbtton = view.findViewById(R.id.okbttn)
        backbtton = view.findViewById(R.id.backbtn)
        mStickerLayout = view.findViewById(R.id.sticker_container)
        iv = view.findViewById(R.id.imgPhotosticker)
        if (bitmap != null) {
            iv.setImageBitmap(bitmap)
        } else {
            iv.setImageURI(selectedUri)
        }
        setRecyclerView()

        btn_removeSticks = view.findViewById(R.id.btn_removeSticks)
        btn_removeSticks!!.setOnClickListener {
            removeStickers()
        }
        okbtton.setOnClickListener {
            val view1 = view.findViewById(R.id.sticker_img_layout) as RelativeLayout
            view1.isDrawingCacheEnabled = true
            view1.buildDrawingCache()
            val bm = view1.drawingCache
            val bitmap = Bitmap.createScaledBitmap(bm, 360, 360, true)


            val intent = Intent()
            intent.action = Constants.INTENT_ACTION_IMAGE_RECEIVED
            intent.putExtra(Constants.INTENT_PARAMETER_EDITED_IMAGE, bitmap)
            activity!!.sendBroadcast(intent)
            findNavController().navigateUp()


        }
        backbtton.setOnClickListener() {
            findNavController().navigateUp()
        }


        fonts = view.findViewById(R.id.fonts)
        fonts!!.visibility = View.GONE
        fonts?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                textEditInfo!!.text = s.toString()
                mStickerLayout?.updateStickerText(textEditInfo)
                mStickerLayout?.postInvalidate()
            }

            override fun afterTextChanged(s: Editable) {}
        })
        mStickerLayout?.setStickerLayoutListener(object : StickerLayout.StickerLayoutListener {
            override fun onStickerLayoutClick() {
                fonts?.setVisibility(View.GONE)
            }

            override fun onStickerTouch(index: Int) {}
            override fun onTextEdit(textEditInfo: TextEditInfo) {
                this@StickerDetailFragment.textEditInfo = textEditInfo
                fonts?.setVisibility(View.VISIBLE)
                fonts?.setText(textEditInfo.text)
            }

            override fun needDisallowInterceptTouchEvent(isNeed: Boolean) {}
        })

    }

    private fun initData() {
        val permissionCheck = activity?.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            activity?.let {
                Utility.copyAssetsFileToDirs(
                    it,
                    "vgmap",
                    rootPath
                )
            }
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    private fun setRecyclerView() {
        var root = File(activity?.baseContext?.filesDir?.path + "/vgmap/sticker1/res")

        val fileList: ArrayList<File> = ArrayList()
        val listAllFiles = root.listFiles()

        if (listAllFiles != null && listAllFiles.size > 0) {
            for (currentFile in listAllFiles) {
                if (currentFile.name.endsWith(".png")) {

                    Log.e("downloadFileName", currentFile.getName())
                    fileList.add(currentFile.absoluteFile)
                    val d = Drawable.createFromPath(currentFile.absolutePath)
                    if (d != null) {
                        filteredImageList.add(d)
                        stikernames.add(currentFile.name)

                    }
                }
            }
            root =
                File(activity?.baseContext?.filesDir?.path + "/vgmap/textArt1/res/flowertext_sticker_1_editable.png")
            val a = (root.absolutePath)
            Drawable.createFromPath(a)?.let { filteredImageList.add(it) }
            stikernames.add(root.name)


        }
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        adapter = CustomRVAdapter(stikernames, filteredImageList, this)

        if (rcyc != null) {
            rcyc!!.setHasFixedSize(true)
            rcyc!!.layoutManager = layoutManager

            rcyc!!.adapter = adapter
            scrollOnUI(15)

        }
    }

    override fun onImgClick(position: Int) {
        super.onImgClick(position)
        val name = stikernames[position]
        postImageStickerTrackerEvent(name)
        if (position != 16) {
             activity?.baseContext?.filesDir?.path + "/vgmap/sticker1/res/" + name
             mStickerLayout!!.addSticker(rootPath + "sticker1", name)

        } else {
           activity?.baseContext?.filesDir?.path + "/vgmap/textArt1/res/" + name
            result = mStickerLayout!!.addSticker(rootPath + "textArt1", "")
        }
    }


    private fun scrollOnUI(pos: Int) {
        runOnUiThread {
            rcyc?.scrollToPosition(pos)
        }


    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return
        activity?.runOnUiThread(action)
    }

    private fun removeStickers() {
        mStickerLayout!!.removeAllSticker()
    }

    companion object {
        private const val TAG = "StickerActivity"
    }


}

