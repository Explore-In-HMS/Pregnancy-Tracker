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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.models.entities.PregnancyInfo
import com.team7.pregnancytracker.models.enums.EPregnancyInfoType
import com.team7.pregnancytracker.utils.Constants

class PregnancyInfoFragment : Fragment() {

    //Variables
    private lateinit var pregnancyInfoType: EPregnancyInfoType
    private lateinit var pregnancyInfo: PregnancyInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pregnancyInfoType =
                it.getSerializable(Constants.FRAGMENT_ARG_PARAM_PREGNANCY_INFO_TYPE) as EPregnancyInfoType
            pregnancyInfo =
                it.getSerializable(Constants.FRAGMENT_ARG_PARAM_PREGNANCY_INFO) as PregnancyInfo
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pregnancy_info, container, false)
        init(view)
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(
            pregnancyInfoType: EPregnancyInfoType,
            pregnancyInfo: PregnancyInfo
        ) =
            PregnancyInfoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(
                        Constants.FRAGMENT_ARG_PARAM_PREGNANCY_INFO_TYPE,
                        pregnancyInfoType
                    )
                    putSerializable(Constants.FRAGMENT_ARG_PARAM_PREGNANCY_INFO, pregnancyInfo)
                }
            }
    }

    private fun init(view: View) {
        val pregnancyInfoImageImageView =
            view.findViewById<ImageView>(R.id.image_view_pregnancy_info_image)
        val pregnancyInfoTextView = view.findViewById<TextView>(R.id.text_view_pregnancy_info)

        if (pregnancyInfoType.equals(EPregnancyInfoType.BABY)) {
            val imageResourceId: Int = context!!.resources.getIdentifier(
                "baby_${pregnancyInfo.getElapsedWeeks()}",
                "drawable",
                context!!.getPackageName()
            )

            if (imageResourceId != 0) {
                pregnancyInfoImageImageView.setImageResource(imageResourceId)
            } else {
                pregnancyInfoImageImageView.visibility = View.GONE
            }
            pregnancyInfoTextView.text = pregnancyInfo.pregnancyInfoData.babyInfo
        } else {
            val imageResourceId: Int = context!!.resources.getIdentifier(
                "mom_${pregnancyInfo.getElapsedWeeks()}",
                "drawable",
                context!!.getPackageName()
            )

            pregnancyInfoImageImageView.setImageResource(imageResourceId)
            pregnancyInfoTextView.text = pregnancyInfo.pregnancyInfoData.momInfo
        }
    }
}