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

package com.team7.pregnancytracker.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.models.entities.Contact
import com.team7.pregnancytracker.models.interfaces.IOnItemSelectListener
import com.team7.pregnancytracker.utils.Constants



class ContactListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private  var contactList: List<Contact>
    private  var itemSelectListener: IOnItemSelectListener




    constructor(contactList: List<Contact>, itemSelectListener: IOnItemSelectListener) {
        this.contactList = contactList
        this.itemSelectListener=itemSelectListener
    }




    class ContactViewHolder : RecyclerView.ViewHolder {
        var Name: TextView
        var Number: TextView
        var dateTextView: TextView
        var callbutton:TextView
        var emergencybutton:TextView

        constructor(itemView: View) : super(itemView) {
            Name = itemView.findViewById(R.id.text_view_name)
            Number = itemView.findViewById(R.id.text_view_number)
            dateTextView = itemView.findViewById(R.id.text_view_last_update_date)
            callbutton=itemView.findViewById(R.id.emergencycall)
            emergencybutton=itemView.findViewById(R.id.emergencylocationsms)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_template_contact, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val contactViewHolder = holder as ContactViewHolder

        val contact = contactList[position]
        contactViewHolder.Name.text=contact.name
        contactViewHolder.Number.text = contact.number
        contactViewHolder.dateTextView.text =
            Constants.periodDateFormatter.format(contact.createDate)

        contactViewHolder.itemView.setOnClickListener {
            itemSelectListener.onItemSelect(contact, position)
        }

         contactViewHolder.callbutton.setOnClickListener(){
             val context: Context =it.context
             val intent = Intent(Intent.ACTION_DIAL)
             intent.data = Uri.parse("tel:$"+contact.number)

                context.startActivity(intent)
         }
        contactViewHolder.emergencybutton.setOnClickListener(){
            itemSelectListener.onbuttonclick(contact.number,position)
        }
    }



}
