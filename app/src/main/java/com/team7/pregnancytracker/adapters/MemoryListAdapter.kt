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

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.models.entities.Memory
import com.team7.pregnancytracker.models.interfaces.IOnItemSelectListener
import com.team7.pregnancytracker.utils.Constants


class MemoryListAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private var memoryList: List<Memory>
    private  var itemSelectListener: IOnItemSelectListener


    constructor(memoryList: List<Memory>,itemSelectListener: IOnItemSelectListener) {
        this.memoryList = memoryList
        this.itemSelectListener=itemSelectListener
    }

    class MemoryViewHolder : RecyclerView.ViewHolder {
        var iconImageView: ImageView
        var titleTextView: TextView
        var dateTextView: TextView

        constructor(itemView: View) : super(itemView) {
            iconImageView = itemView.findViewById(R.id.image_view_icon)
            titleTextView = itemView.findViewById(R.id.text_view_title)
            dateTextView = itemView.findViewById(R.id.text_view_last_update_date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_template_diary, parent, false)
        return MemoryViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return memoryList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val memoryViewHolder = holder as MemoryViewHolder

        val memory = memoryList[position]
        val bmp = BitmapFactory.decodeByteArray(memory.photo, 0, memory.photo.size)


        memoryViewHolder.iconImageView.setImageBitmap(bmp)
        memoryViewHolder.titleTextView.text = memory.title
        memoryViewHolder.dateTextView.text =
            Constants.periodDateFormatter.format(memory.createDate)

        memoryViewHolder.itemView.setOnClickListener {
            itemSelectListener.onItemSelect(memory, position)
        }

    }

}