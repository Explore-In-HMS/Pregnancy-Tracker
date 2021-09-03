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

import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.getFromSharedPreferences
import com.team7.pregnancytracker.models.entities.PregnancyInfo
import com.team7.pregnancytracker.models.enums.EPregnancyInfoType
import com.team7.pregnancytracker.utils.Constants
import com.team7.pregnancytracker.widgets.PregnancyAppTextView


class PregnancyFragment : Fragment() {

    //Class Constants
    private val ANIMATION_DURATION: Long = 500

    //Widgets
    private lateinit var infoHeaderBackgroundImageView: ImageView
    private lateinit var infoHeaderIconImageView: ImageView

    //Class Variables
    private lateinit var infoHeaderBackgroundTransitionDrawable: TransitionDrawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_pregnancy, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        val lastPeriodDateText = getFromSharedPreferences(
            activity!!,
            Constants.SHARED_PREFERENCES_KEY_LAST_PERIOD_DATE,
            ""
        ) as String

        val pregnancyInfo = PregnancyInfo(context!!, lastPeriodDateText)

        initData(view, pregnancyInfo)
        initHeader(view)
        initViewPager(view, pregnancyInfo)

        firstHeaderAnimation()
    }

    private fun initData(view: View, pregnancyInfo: PregnancyInfo) {
        val weekTextView = view.findViewById<TextView>(R.id.text_view_week)
        val dayTextView = view.findViewById<TextView>(R.id.text_view_day)
        val trimesterTextView = view.findViewById<TextView>(R.id.text_view_trimester)
        val remainingDaysTextView = view.findViewById<TextView>(R.id.text_view_remaining_days)
        val babySizeTextView = view.findViewById<TextView>(R.id.text_view_baby_size)

        weekTextView.text = pregnancyInfo.getElapsedWeeks().toString()
        dayTextView.text = pregnancyInfo.elapsedDays.toString()
        trimesterTextView.text = pregnancyInfo.getTrimester().toString()
        remainingDaysTextView.text = pregnancyInfo.getRemainingDaysText(context!!)
        babySizeTextView.text = pregnancyInfo.getBabySizeText(context!!)
    }

    private fun initHeader(view: View) {
        infoHeaderBackgroundImageView = view.findViewById(R.id.image_view_info_header_background)
        val draws = arrayOf(
            context!!.getDrawable(R.drawable.background_pregnancy_info_baby_header),
            context!!.getDrawable(R.drawable.background_pregnancy_info_mom_header)
        )
        infoHeaderBackgroundTransitionDrawable = TransitionDrawable(draws)
        infoHeaderBackgroundImageView.setImageDrawable(infoHeaderBackgroundTransitionDrawable)

        infoHeaderIconImageView = view.findViewById(R.id.image_view_info_header_icon)
    }

    private fun firstHeaderAnimation() {
        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = ANIMATION_DURATION
        infoHeaderIconImageView.startAnimation(alphaAnimation)
    }

    private fun animateHeader(pregnancyInfoType: EPregnancyInfoType) {
        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.duration = ANIMATION_DURATION
        alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                val alphaAnimation = AlphaAnimation(0f, 1f)
                alphaAnimation.duration = ANIMATION_DURATION
                infoHeaderIconImageView.startAnimation(alphaAnimation)

                if (pregnancyInfoType.equals(EPregnancyInfoType.BABY)) {
                    infoHeaderIconImageView.setImageResource(R.drawable.im_baby)
                } else {
                    infoHeaderIconImageView.setImageResource(R.drawable.im_mom)
                }
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }
        })
        infoHeaderIconImageView.startAnimation(alphaAnimation)

        if (pregnancyInfoType.equals(EPregnancyInfoType.BABY)) {
            infoHeaderBackgroundTransitionDrawable.reverseTransition((ANIMATION_DURATION * 2).toInt())
        } else {
            infoHeaderBackgroundTransitionDrawable.startTransition((ANIMATION_DURATION * 2).toInt())
        }
    }

    private fun initViewPager(view: View, pregnancyInfo: PregnancyInfo) {
        val babyInfoFragment =
            PregnancyInfoFragment.newInstance(EPregnancyInfoType.BABY, pregnancyInfo)
        val momInfoFragment =
            PregnancyInfoFragment.newInstance(EPregnancyInfoType.MOM, pregnancyInfo)
        val fragmentPagerAdapter = object : FragmentPagerAdapter(
            childFragmentManager,
            FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            val fragments = arrayOf(babyInfoFragment, momInfoFragment)

            val fragmentNames = arrayOf(
                getString(R.string.text_your_baby),
                getString(R.string.text_your_life)
            )

            override fun getCount(): Int {
                return fragments.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return fragmentNames.get(position)
            }

            override fun getItem(position: Int): Fragment {
                return fragments.get(position)
            }
        }

        val viewPager = view.findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = fragmentPagerAdapter
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
        for (i in 0 until tabLayout.tabCount) {
            val tabViewTextView = PregnancyAppTextView(context)
            tabViewTextView.text = tabLayout.getTabAt(i)!!.text
            tabViewTextView.setTextColor(resources.getColorStateList(R.color.color_style_tab_text))
            tabViewTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            tabLayout.getTabAt(i)!!.customView = tabViewTextView
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    animateHeader(EPregnancyInfoType.BABY)
                } else {
                    animateHeader(EPregnancyInfoType.MOM)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }
}