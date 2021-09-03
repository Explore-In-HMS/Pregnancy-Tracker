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

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.DatabaseHelper
import com.team7.pregnancytracker.models.entities.Contact
import com.team7.pregnancytracker.utils.Constants
import com.team7.pregnancytracker.widgets.PregnancyAppButton
import java.util.*


class ContactInfoFragment : Fragment() {


    companion object {
        const val FRAGMENT_ARG_PARAM_CONTACT = "contact"
    }

    private var contact: Contact? = null

    private lateinit var contactTitleEditText: EditText
    private lateinit var contactnumberEditText: EditText
    private lateinit var backbutton:ImageButton
    private lateinit var updatebutton:PregnancyAppButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contact = it.getSerializable(FRAGMENT_ARG_PARAM_CONTACT) as Contact?
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_contact_info, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        contactTitleEditText = view.findViewById(R.id.edit_text_contact_title)
        contactnumberEditText = view.findViewById(R.id.edit_text_contact_info_number)
        val deleteButton = view.findViewById<Button>(R.id.button_delete)
        updatebutton=view.findViewById(R.id.button_update)
        backbutton=view.findViewById(R.id.image_button_back)


        if (contact != null) {
            contactTitleEditText.setText(contact!!.name)
            contactnumberEditText.setText(contact!!.number)
            view.findViewById<TextView>(R.id.text_view_create_date).text =
                Constants.periodDateFormatter.format(contact!!.createDate)


            deleteButton.visibility = View.VISIBLE
        } else {
            deleteButton.visibility = View.GONE
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
                    DatabaseHelper.getDatabaseHelper(context!!)!!.contactDao().delete(contact!!)
                    findNavController().navigateUp()
                }
                .setNegativeButton(
                    getString(R.string.text_dialog_button_no)
                ) { dialogInterface: DialogInterface?, i: Int -> }
                .show()
        }
        updatebutton.setOnClickListener() {
                val name = contactTitleEditText.text.toString()
                val number = contactnumberEditText.text.toString()
                val currentTime = Calendar.getInstance().timeInMillis

                contact!!.name=name
                contact!!.number=number
                contact!!.createDate=currentTime

            DatabaseHelper.getDatabaseHelper(context!!)!!.contactDao().update(contact!!)
            findNavController().navigateUp()
        }
        backbutton.setOnClickListener{
            findNavController().navigateUp()

        }
    }


}