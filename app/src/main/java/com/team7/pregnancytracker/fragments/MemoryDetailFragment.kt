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

import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.DatabaseHelper
import com.team7.pregnancytracker.helpers.postButtonTrackerEvent
import com.team7.pregnancytracker.models.entities.Memory
import com.team7.pregnancytracker.utils.Constants
import java.io.OutputStream
import java.lang.Exception


class MemoryDetailFragment : Fragment() {
    companion object {
        const val FRAGMENT_ARG_PARAM_MEMORY = "memory"
    }

    private var memory: Memory? = null


    private lateinit var memoryTitleEditText: EditText
    private lateinit var memoryImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            memory = it.getSerializable(FRAGMENT_ARG_PARAM_MEMORY) as Memory?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_memory_detail, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        memoryTitleEditText = view.findViewById(R.id.edit_text_memory_title)
        memoryImageView = view.findViewById(R.id.image_view_mood)
        val savetophone = view.findViewById<Button>(R.id.button_savephone)
        val deleteButton = view.findViewById<Button>(R.id.button_delete)

        if (memory != null) {
            memoryTitleEditText.setText(memory!!.title)
            view.findViewById<TextView>(R.id.text_view_create_date).text =
                Constants.periodDateFormatter.format(memory!!.createDate)
            val bmp = BitmapFactory.decodeByteArray(memory!!.photo, 0, memory!!.photo.size)

            memoryImageView.setImageBitmap(bmp)
            deleteButton.visibility = View.VISIBLE
        } else {
            savetophone.text = getString(R.string.text_button_save)
            deleteButton.visibility = View.GONE
        }
        savetophone.setOnClickListener() {
            val bitmap = (memoryImageView.drawable as BitmapDrawable).bitmap
            val values = ContentValues()
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/test_pictures")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "img_${SystemClock.uptimeMillis()}")

            val uri: Uri? =
                context!!.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
            if (uri != null) {
                saveImageToStream(bitmap, context!!.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context!!.contentResolver.update(uri, values, null, null)
            }


        }

        deleteButton.setOnClickListener {
            AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.text_dialog_title_delete))
                .setMessage(getString(R.string.text_dialog_description_delete))
                .setPositiveButton(getString(R.string.text_dialog_button_yes)) { dialog: DialogInterface?, which: Int ->
                    Toast.makeText(
                        context!!,
                        R.string.text_toast_operation_successful,
                        Toast.LENGTH_SHORT
                    ).show()
                    postButtonTrackerEvent("Image Delete")
                    DatabaseHelper.getDatabaseHelper(context!!)!!.memoryDao().delete(memory!!)
                    findNavController().navigateUp()
                }
                .setNegativeButton(
                    getString(R.string.text_dialog_button_no)
                ) { dialogInterface: DialogInterface?, i: Int -> }
                .show()
        }
    }


    fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
                Toast.makeText(getActivity(), "Succcess!", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}