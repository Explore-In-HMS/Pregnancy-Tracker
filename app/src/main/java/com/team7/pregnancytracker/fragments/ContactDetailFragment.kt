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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.DatabaseHelper
import com.team7.pregnancytracker.models.entities.Contact
import java.util.*


class ContactDetailFragment : Fragment() {
    private lateinit var contactName: EditText
    private lateinit var contactNumber: EditText
    private lateinit var savecontactbutton: Button
    private lateinit var backbutton:ImageButton

    private var contact: Contact? = null
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
        val view =inflater.inflate(R.layout.fragment_contact_detail, container, false)
        init(view)
        return view
    }
     fun init(view: View){
        contactName=view.findViewById(R.id.edit_text_contact_name)
         contactNumber=view.findViewById(R.id.edit_text_contact_number)
         savecontactbutton=view.findViewById(R.id.button_savecontact)
         backbutton=view.findViewById(R.id.image_button_back)
         savecontactbutton.setOnClickListener{

                 val name=contactName.text.toString()
                 val number=contactNumber.text.toString()
                 val currentTime = Calendar.getInstance().timeInMillis

                 val contact = Contact(
                     name,
                     number,
                     currentTime

                 )

                 DatabaseHelper.getDatabaseHelper(context!!)!!.contactDao().insert(contact)
                 Toast.makeText(getActivity(),"Success!", Toast.LENGTH_SHORT).show()
                 findNavController().navigateUp()


         }
         backbutton.setOnClickListener{
             findNavController().navigateUp()

         }
    }
    companion object {
        const val FRAGMENT_ARG_PARAM_CONTACT = "contact"
    }
}