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

package com.team7.pregnancytracker.helpers
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.team7.pregnancytracker.R
import kotlinx.android.synthetic.main.filter_card_item.view.*

class CustomRVAdapter (
    private val titles: List<String>,
    private val filteredImages: List<Drawable>,
    onImgClickListener: OnImgClickListener
) :
    RecyclerView.Adapter<CustomRVAdapter.ViewHolder>() {

    private var mOnImgClickListener: OnImgClickListener
    private val items: MutableList<CardView>

    init {
        this.items = ArrayList()
        mOnImgClickListener = onImgClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.filter_card_item, parent, false)
        return ViewHolder(v, mOnImgClickListener)
    }

    override fun getItemCount(): Int {
        return filteredImages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.initialize(titles[position], filteredImages[position], mOnImgClickListener)
        items.add(holder.card)
    }

    class ViewHolder(itemView: View, val onImgClickListener: OnImgClickListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val titleTV: TextView = itemView.titleTextView
        val card: CardView = itemView.cardView
        val filterIV: ImageView = itemView.filterImageView

        fun initialize(imgFilterName: String, filteredImg: Drawable, action: OnImgClickListener) {
            titleTV.text = imgFilterName
            filterIV.setImageBitmap(filteredImg.toBitmap())
            itemView.setOnClickListener {
                action.onImgClick(adapterPosition)
            }
        }

        override fun onClick(view: View?) {
            itemView.setOnClickListener(this)
            onImgClickListener.onImgClick(adapterPosition)
        }


    }

    public interface OnImgClickListener {
        fun onImgClick(position: Int) {

        }
    }
}