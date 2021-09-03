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
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.activities.MainActivity
import com.team7.pregnancytracker.adapters.DiaryListAdapter
import com.team7.pregnancytracker.adapters.MemoryListAdapter
import com.team7.pregnancytracker.helpers.DatabaseHelper
import com.team7.pregnancytracker.helpers.postDiaryTrackerEvent
import com.team7.pregnancytracker.models.entities.Diary
import com.team7.pregnancytracker.models.entities.Memory
import com.team7.pregnancytracker.models.enums.EDiaryType
import com.team7.pregnancytracker.models.interfaces.IOnItemSelectListener
import com.team7.pregnancytracker.utils.Constants

class DiaryFragment : Fragment() {

    //Variables
    private val TAG = DiaryFragment::class.java.simpleName

    //Widgets
    private lateinit var diaryRecyclerView: RecyclerView
    private lateinit var noDiaryView: View
    private lateinit var addDiaryFab: ExtendedFloatingActionButton
    private lateinit var addMoodView: View
    private lateinit var addImageView: View
    private lateinit var addNoteView: View
    private lateinit var addUltrasoundView: View

    //Class Variables
    private var isAddMenuVisible = false
    private var menuSize: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_diary, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        menuSize =
            (resources.getDimension(R.dimen.size_large) + resources.getDimension(R.dimen.margin_container_small)) * -1

        noDiaryView = view.findViewById(R.id.view_no_diary)

        val diaryList = DatabaseHelper.getDatabaseHelper(activity!!)!!.diaryDao().getAllDiaries()
        val memoryList = DatabaseHelper.getDatabaseHelper(activity!!)!!.memoryDao().getAllMemories()
        diaryRecyclerView = view.findViewById(R.id.recycler_view_diary)
        val diaryListAdapter =
            DiaryListAdapter(context!!, diaryList, object : IOnItemSelectListener {
                override fun onItemSelect(item: Any, position: Int) {
                    val bundle = Bundle()
                    bundle.putSerializable(Constants.FRAGMENT_ARG_PARAM_DIARY, item as Diary)
                    bundle.putSerializable(
                        Constants.FRAGMENT_ARG_PARAM_DIARY_TYPE,
                        EDiaryType.valueOf(item.type)
                    )
                    findNavController().navigate(
                        R.id.action_navigation_diary_to_navigation_diary_details,
                        bundle
                    )
                }

                override fun onbuttonclick(item: Any, position: Int) {

                }
            })

        val memoryListAdapter = MemoryListAdapter(memoryList, object : IOnItemSelectListener {
            override fun onItemSelect(item: Any, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable(
                    MemoryDetailFragment.FRAGMENT_ARG_PARAM_MEMORY,
                    item as Memory
                )
                findNavController().navigate(
                    R.id.action_navigation_diary_to_navigation_memory_detail,
                    bundle
                )
            }

            override fun onbuttonclick(item: Any, position: Int) {

            }
        })

        val concatAdapter = ConcatAdapter(diaryListAdapter, memoryListAdapter)

        diaryRecyclerView.itemAnimator = DefaultItemAnimator()
        diaryRecyclerView.adapter = concatAdapter

        if (diaryList.isEmpty() && memoryList.isEmpty()) {
            noDiaryView.visibility = View.VISIBLE
            diaryRecyclerView.visibility = View.GONE
        } else {
            noDiaryView.visibility = View.GONE
            diaryRecyclerView.visibility = View.VISIBLE
        }

        addMoodView = view.findViewById(R.id.view_add_mood)
        addImageView = view.findViewById(R.id.view_add_image)
        addNoteView = view.findViewById(R.id.view_add_note)
        addUltrasoundView = view.findViewById(R.id.view_add_ultrasound)

        addDiaryFab = view.findViewById(R.id.fab_add_diary)
        addDiaryFab.shrink()
        addDiaryFab.setOnClickListener {
            if (isAddMenuVisible) {
                hideAddMenu()
            } else {
                showAddMenu()
            }
        }

        addMoodView.setOnClickListener {
            hideAddMenu()
            val bundle = Bundle()
            bundle.putSerializable(Constants.FRAGMENT_ARG_PARAM_DIARY_TYPE, EDiaryType.MOOD_TRACKER)
            findNavController().navigate(
                R.id.action_navigation_diary_to_navigation_diary_details,
                bundle
            )
            postDiaryTrackerEvent(EDiaryType.MOOD_TRACKER)
        }

        addImageView.setOnClickListener {
            hideAddMenu()
            findNavController().navigate(R.id.action_navigation_diary_to_navigation_image_details)
            postDiaryTrackerEvent(EDiaryType.IMAGE_EDITOR)
        }

        addNoteView.setOnClickListener {
            hideAddMenu()
            Toast.makeText(activity!!, "add note", Toast.LENGTH_SHORT).show()
            postDiaryTrackerEvent(EDiaryType.NOTE)
        }

        addUltrasoundView.setOnClickListener {
            hideAddMenu()
            val bundle = Bundle()
            bundle.putSerializable(Constants.FRAGMENT_ARG_PARAM_DIARY_TYPE, EDiaryType.ULTRASOUND)
            findNavController().navigate(
                R.id.action_navigation_diary_to_navigation_diary_details,
                bundle
            )
            postDiaryTrackerEvent(EDiaryType.ULTRASOUND)
        }

        addMoodView.isClickable = false
        addImageView.isClickable = false
        addNoteView.isClickable = false
        addUltrasoundView.isClickable = false
    }

    private fun showAddMenu() {
        addDiaryFab.extend()
        isAddMenuVisible = true
        addMoodView.animate().translationY(menuSize).alpha(1f)
        addImageView.animate().translationY(menuSize * 2).alpha(1f)
        addNoteView.animate().translationY(menuSize * 3).alpha(1f)
        addUltrasoundView.animate().translationY(menuSize * 4).alpha(1f)

        addMoodView.isClickable = true
        addImageView.isClickable = true
        addNoteView.isClickable = true
        addUltrasoundView.isClickable = true
    }

    private fun hideAddMenu() {
        addDiaryFab.shrink()
        isAddMenuVisible = false
        addMoodView.animate().translationY(0f).alpha(0f)
        addImageView.animate().translationY(0f).alpha(0f)
        addNoteView.animate().translationY(0f).alpha(0f)
        addUltrasoundView.animate().translationY(0f).alpha(0f)

        addMoodView.isClickable = false
        addImageView.isClickable = false
        addNoteView.isClickable = false
        addUltrasoundView.isClickable = false
    }
}