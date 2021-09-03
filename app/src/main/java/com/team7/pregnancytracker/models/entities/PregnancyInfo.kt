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

package com.team7.pregnancytracker.models.entities

import android.content.Context
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.ExternalDatabaseHelper
import com.team7.pregnancytracker.utils.Constants
import java.io.Serializable
import java.util.*

class PregnancyInfo : Serializable {

    var pregnancyInfoData: PregnancyInfoData

    var lastPeriodDateString: String = ""

    var elapsedDays: Int = 0

    constructor(context: Context, lastPeriodDateString: String) {
        this.lastPeriodDateString = lastPeriodDateString
        calculateElapsedDays()

        pregnancyInfoData = ExternalDatabaseHelper.getDatabaseHelper(context)!!.pregnancyInfoDao()
            .getPregnancyInfo(getElapsedWeeks())
    }

    private fun calculateElapsedDays() {
        val currentCalendar = Calendar.getInstance()
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0)
        currentCalendar.set(Calendar.MINUTE, 0)
        currentCalendar.set(Calendar.SECOND, 0)
        currentCalendar.set(Calendar.MILLISECOND, 0)
        val lastPeriodDateCalendar = Calendar.getInstance()
        lastPeriodDateCalendar.time = Constants.periodDateFormatter.parse(lastPeriodDateString)

        val dif: Long = currentCalendar.timeInMillis - lastPeriodDateCalendar.timeInMillis
        elapsedDays = (dif / (24 * 60 * 60 * 1000)).toInt()
        if (elapsedDays > 280) {
            elapsedDays = 280
        }
    }

    fun getElapsedWeeks(): Int {
        return elapsedDays / 7
    }

    fun getTrimester(): Int {
        return ((getElapsedWeeks() - 1) / 13) + 1
    }

    fun getRemainingDaysText(context: Context): String {
        return context.resources.getQuantityString(
            R.plurals.text_remaining_days,
            (280 - elapsedDays), (280 - elapsedDays)
        )
    }

    fun getBabySizeText(context: Context): String {
        if (pregnancyInfoData.babySize.isNullOrEmpty()) {
            return context.getString(R.string.text_too_early)
        }
        return context.getString(R.string.text_baby_size, pregnancyInfoData.babySize)
    }
}