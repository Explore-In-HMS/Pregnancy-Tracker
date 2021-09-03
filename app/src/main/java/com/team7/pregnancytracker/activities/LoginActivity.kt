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

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.entity.auth.Scope
import com.huawei.hms.support.api.entity.common.CommonConstant
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.getFromSharedPreferences
import com.team7.pregnancytracker.helpers.putToSharedPreferences
import com.team7.pregnancytracker.models.entities.Profile
import com.team7.pregnancytracker.utils.Constants
import org.json.JSONException

class LoginActivity : AppCompatActivity() {

    //Class Constants
    private val TAG = LoginActivity::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.button_huawei_login).setOnClickListener {
            huaweiSignIn()
        }
    }

    override fun onStart() {
        super.onStart()
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        val userProfile = try {
            Profile(
                getFromSharedPreferences(
                    this,
                    Constants.SHARED_PREFERENCES_KEY_USER_PROFILE,
                    ""
                ) as String
            )
        } catch (e: JSONException) {
            null
        }

        if (userProfile != null) {
            checkDateSelect(userProfile)
        }
    }

    private fun checkDateSelect(profile: Profile) {
        val lastPeriodDateText = getFromSharedPreferences(
            this,
            Constants.SHARED_PREFERENCES_KEY_LAST_PERIOD_DATE,
            ""
        ) as String
        if (lastPeriodDateText.isEmpty()) {
            openDateSelect(profile)
        } else {
            openApp(profile)
        }
    }

    private fun openDateSelect(profile: Profile) {
        val dateSelectIntent = Intent(this, DateSelectActivity::class.java)
        dateSelectIntent.putExtra(Constants.INTENT_PARAMETER_PROFILE, profile)
        startActivity(dateSelectIntent)
        finish()
    }

    private fun openApp(profile: Profile) {
        val appIntent = Intent(this, MainActivity::class.java)
        appIntent.putExtra(Constants.INTENT_PARAMETER_PROFILE, profile)
        startActivity(appIntent)
        finish()
    }

    private fun huaweiSignIn() {
        val scopeList = ArrayList<Scope>()
        scopeList.add(Scope(CommonConstant.SCOPE.SCOPE_ACCOUNT_EMAIL))
        scopeList.add(Scope(CommonConstant.SCOPE.ACCOUNT_BASEPROFILE))
        val huaweiIdAuthParams =
            HuaweiIdAuthParamsHelper(HuaweiIdAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setEmail()
                .setProfile().setScopeList(scopeList).setIdToken().createParams()
        val hmsService = HuaweiIdAuthManager.getService(this, huaweiIdAuthParams)
        startActivityForResult(
            hmsService.signInIntent,
            Constants.ACTIVITY_REQUEST_CODE_HUAWEI_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.ACTIVITY_REQUEST_CODE_HUAWEI_SIGN_IN) {
            val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
            if (authHuaweiIdTask.isSuccessful) {
                Log.d(TAG, "Huawei sign in was completed successfully.")
                val huaweiAccount = authHuaweiIdTask.result
                if (huaweiAccount.email.isNullOrEmpty()) {
                    Log.d(TAG, "No email in huawei account.")
                    Toast.makeText(this, R.string.text_toast_email_error, Toast.LENGTH_LONG).show()
                } else {
                    val userProfile = Profile(
                        huaweiAccount.displayName,
                        huaweiAccount.email,
                        huaweiAccount.avatarUriString
                    )
                    putToSharedPreferences(
                        this,
                        Constants.SHARED_PREFERENCES_KEY_USER_PROFILE,
                        userProfile
                    )
                    checkDateSelect(userProfile)
                }
            } else {
                Log.d(
                    TAG,
                    "Huawei sign in failed: " + (authHuaweiIdTask.getException() as ApiException).getStatusCode()
                )
            }
        }
    }
}