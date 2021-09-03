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
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.models.entities.Diary
import com.team7.pregnancytracker.models.enums.EDiaryType
import com.team7.pregnancytracker.models.interfaces.IOnItemSelectListener
import com.team7.pregnancytracker.modules.moodTracker.EMood
import com.team7.pregnancytracker.utils.Constants

class DiaryListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //Class Variables
    private var context: Context
    private var diaryList: List<Diary>
    private var itemSelectListener: IOnItemSelectListener

    constructor(
        context: Context,
        diaryList: List<Diary>,
        itemSelectListener: IOnItemSelectListener
    ) {
        this.context = context
        this.diaryList = diaryList
        this.itemSelectListener = itemSelectListener
    }

    class DiaryViewHolder : RecyclerView.ViewHolder {
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
        return DiaryViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return diaryList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val diaryViewHolder = holder as DiaryViewHolder

        val diary = diaryList.get(position)

        diaryViewHolder.titleTextView.text = diary.title
        diaryViewHolder.dateTextView.text =
            Constants.periodDateFormatter.format(diary.lastUpdateDate)

        if (diary.type.equals(EDiaryType.MOOD_TRACKER.toString())) {
            diaryViewHolder.iconImageView.setImageResource(EMood.valueOf(diary.mood).iconResourceId)
        } else if (diary.type.equals(EDiaryType.ULTRASOUND.toString())) {
            diaryViewHolder.iconImageView.setImageURI(Uri.parse(diary.filePath))
            if (diaryViewHolder.iconImageView.drawable == null) {
                diaryViewHolder.iconImageView.setImageResource(R.drawable.icon_exclamation_mark)
            }
        }

        diaryViewHolder.itemView.setOnClickListener {
            itemSelectListener.onItemSelect(diary, position)
        }
    }
}