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

package com.team7.pregnancytracker.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.postDateSetterEvent
import com.team7.pregnancytracker.helpers.putToSharedPreferences
import com.team7.pregnancytracker.utils.Constants
import java.util.*


class DateSelectActivity : AppCompatActivity() {

    //Widgets
    private lateinit var lastPeriodDateEditText: EditText
    private lateinit var estimatedBirthDateEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_select)

        lastPeriodDateEditText = findViewById(R.id.edit_text_last_period_date)
        estimatedBirthDateEditText = findViewById(R.id.edit_text_estimated_birth_date)

        lastPeriodDateEditText.setOnClickListener {
            showDateDialog(true)
        }

        estimatedBirthDateEditText.setOnClickListener {
            showDateDialog(false)
        }

        findViewById<Button>(R.id.button_continue).setOnClickListener {
            if (lastPeriodDateEditText.text.toString().isEmpty()
                && estimatedBirthDateEditText.text.toString().isEmpty()
            ) {
                Toast.makeText(
                    this,
                    getString(R.string.text_toast_empty_field_error),
                    Toast.LENGTH_LONG
                )
                    .show()
            } else {
                val lastPeriodDateText = lastPeriodDateEditText.text.toString()
                putToSharedPreferences(
                    this,
                    Constants.SHARED_PREFERENCES_KEY_LAST_PERIOD_DATE,
                    lastPeriodDateText
                )
                Toast.makeText(this, R.string.text_toast_operation_successful, Toast.LENGTH_LONG)
                    .show()

                val appIntent = Intent(this, MainActivity::class.java)
                appIntent.putExtra(
                    Constants.INTENT_PARAMETER_PROFILE,
                    intent.getSerializableExtra(Constants.INTENT_PARAMETER_PROFILE)
                )
                startActivity(appIntent)
                finish()
            }
        }
    }

    private fun showDateDialog(isTodayMax: Boolean) {//true => last period date, false => estimated birth date of baby
        val todayCalendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { datePicker, year, month, day ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.YEAR, year)
                selectedCalendar.set(Calendar.MONTH, month)
                selectedCalendar.set(Calendar.DAY_OF_MONTH, day)
                if (isTodayMax) {
                    postDateSetterEvent("Last Period Date")

                    lastPeriodDateEditText.setText(
                        Constants.periodDateFormatter.format(
                            selectedCalendar.timeInMillis
                        )
                    )
                    selectedCalendar.add(Calendar.DAY_OF_YEAR, 280)
                    estimatedBirthDateEditText.setText(
                        Constants.periodDateFormatter.format(
                            selectedCalendar.timeInMillis
                        )
                    )
                } else {
                    postDateSetterEvent("Estimated Birth Date")

                    estimatedBirthDateEditText.setText(
                        Constants.periodDateFormatter.format(
                            selectedCalendar.timeInMillis
                        )
                    )
                    selectedCalendar.add(Calendar.DAY_OF_YEAR, -280)
                    lastPeriodDateEditText.setText(
                        Constants.periodDateFormatter.format(
                            selectedCalendar.timeInMillis
                        )
                    )
                }
            },
            todayCalendar.get(Calendar.YEAR),
            todayCalendar.get(Calendar.MONTH),
            todayCalendar.get(Calendar.DAY_OF_MONTH)
        )
        if (isTodayMax) {
            datePickerDialog.datePicker.maxDate = todayCalendar.timeInMillis
            todayCalendar.add(Calendar.DAY_OF_YEAR, -294)
            datePickerDialog.datePicker.minDate = todayCalendar.timeInMillis
        } else {
            datePickerDialog.datePicker.minDate = todayCalendar.timeInMillis
            todayCalendar.add(Calendar.DAY_OF_YEAR, 294)
            datePickerDialog.datePicker.maxDate = todayCalendar.timeInMillis
        }
        datePickerDialog.show()
    }
}