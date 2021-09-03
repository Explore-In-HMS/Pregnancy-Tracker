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

import android.Manifest
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.huawei.hms.iap.Iap
import com.huawei.hms.iap.IapApiException
import com.huawei.hms.iap.entity.*
import com.huawei.hms.iap.util.IapClientHelper
import com.huawei.hms.panorama.Panorama
import com.huawei.hms.panorama.PanoramaInterface
import com.huawei.hms.support.api.client.ResultCallback
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.activities.MainActivity
import com.team7.pregnancytracker.helpers.DatabaseHelper
import com.team7.pregnancytracker.helpers.SecurityHelper
import com.team7.pregnancytracker.helpers.postButtonTrackerEvent
import com.team7.pregnancytracker.models.entities.Diary
import com.team7.pregnancytracker.models.enums.EDiaryType
import com.team7.pregnancytracker.modules.moodTracker.EMood
import com.team7.pregnancytracker.modules.moodTracker.LiveMoodDetectionActivity
import com.team7.pregnancytracker.utils.Constants
import org.json.JSONException
import java.io.IOException
import java.util.*


class DiaryDetailFragment : Fragment() {

    //Class Variables
    private val TAG = DiaryDetailFragment::class.java.simpleName
    private val PERMISSION_REQUEST_CODE_STORAGE = 53
    private val ACTIVITY_REQUEST_CODE_CHECK_IAP = 5353
    private val ACTIVITY_REQUEST_CODE_PURCHASE = 5334

    //Variables
    private var isStoragePermissionRequestFirstTime = false
    private var diary: Diary? = null
    private var diaryType: EDiaryType? = null
    private var is3dViewFeatureOwned = false
    private var iapServiceControl = false

    //Widgets
    private lateinit var diaryTitleEditText: EditText
    private lateinit var moodImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            diary = it.getSerializable(Constants.FRAGMENT_ARG_PARAM_DIARY) as Diary?
            diaryType = it.getSerializable(Constants.FRAGMENT_ARG_PARAM_DIARY_TYPE) as EDiaryType
        }
        if (diaryType == EDiaryType.MOOD_TRACKER) {
            activity!!.registerReceiver(
                moodReceiver,
                IntentFilter(Constants.INTENT_ACTION_MOOD_RECEIVED)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (diaryType == EDiaryType.MOOD_TRACKER) {
            activity!!.unregisterReceiver(moodReceiver)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_diary_details, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        view.findViewById<ImageButton>(R.id.image_button_back).setOnClickListener {
            findNavController().navigateUp()
        }

        diaryTitleEditText = view.findViewById(R.id.edit_text_diary_title)
        moodImageView = view.findViewById(R.id.image_view_mood)
        val continueButton = view.findViewById<Button>(R.id.button_continue)
        val deleteButton = view.findViewById<Button>(R.id.button_delete)
        val view3d = view.findViewById<Button>(R.id.button_view_3d)

        if (diaryType == EDiaryType.ULTRASOUND) {
            moodImageView.setImageResource(R.drawable.icon_ultrasound)
        }

        if (diary != null) {
            diaryTitleEditText.setText(diary!!.title)
            view.findViewById<TextView>(R.id.text_view_create_date).text =
                Constants.periodDateFormatter.format(diary!!.createDate)
            view.findViewById<TextView>(R.id.text_view_last_update_date).text =
                Constants.periodDateFormatter.format(diary!!.lastUpdateDate)
            continueButton.text = getString(R.string.text_button_update)
            deleteButton.visibility = View.VISIBLE

            if (diaryType == EDiaryType.MOOD_TRACKER) {
                moodImageView.setImageResource(EMood.valueOf(diary!!.mood).iconResourceId)
                moodImageView.tag = diary!!.mood
            } else if (diaryType == EDiaryType.ULTRASOUND) {
                view3d.visibility = View.VISIBLE
                moodImageView.setImageURI(Uri.parse(diary!!.filePath))
                moodImageView.tag = "ultrasound"
                if (moodImageView.drawable == null) {
                    moodImageView.setImageResource(R.drawable.icon_exclamation_mark)
                    moodImageView.tag = null
                    view3d.visibility = View.GONE
                }

                checkIAPService()
            }
        } else {
            continueButton.text = getString(R.string.text_button_save)
            deleteButton.visibility = View.GONE
        }

        moodImageView.setOnClickListener {
            if (diaryType == EDiaryType.MOOD_TRACKER) {
                val intent = Intent(activity!!, LiveMoodDetectionActivity::class.java)
                startActivity(intent)
            } else if (diaryType == EDiaryType.ULTRASOUND) {
                isStoragePermissionRequestFirstTime = true
                pickImageFromGallery()
            }
        }

        view3d.setOnClickListener {
            if (is3dViewFeatureOwned) {
                view3d()
            } else {
                if (iapServiceControl) {
                    openPurchasePage()
                } else {
                    Toast.makeText(context, R.string.text_toast_unexpected_error, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        view.findViewById<TextView>(R.id.text_view_title).setOnClickListener {
            if (is3dViewFeatureOwned) {
                //checkOwnedPurchasesForConsume()
            }
        }

        continueButton.setOnClickListener {
            val diaryTitle = diaryTitleEditText.text.toString()
            if (diaryTitle.isEmpty() || moodImageView.tag == null) {
                Toast.makeText(activity!!, R.string.text_toast_empty_field_error, Toast.LENGTH_LONG)
                    .show()
            } else {
                if (diary == null) {
                    addDiary(diaryTitle)
                } else {
                    updateDiary(diaryTitle)
                }
            }
        }

        deleteButton.setOnClickListener {
            AlertDialog.Builder(context!!)
                .setTitle(getString(R.string.text_dialog_title_delete))
                .setMessage(getString(R.string.text_dialog_description_delete))
                .setPositiveButton(getString(R.string.text_dialog_button_yes)) { dialog: DialogInterface?, which: Int ->
                    Toast.makeText(
                        context!!,
                        R.string.text_toast_operation_successful,
                        Toast.LENGTH_SHORT
                    ).show()
                    postButtonTrackerEvent("Mood Delete")
                    DatabaseHelper.getDatabaseHelper(context!!)!!.diaryDao().delete(diary!!)
                    findNavController().navigateUp()
                }
                .setNegativeButton(
                    getString(R.string.text_dialog_button_no)
                ) { dialogInterface: DialogInterface?, i: Int -> }
                .show()
        }
    }

    private val moodReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val mood = intent.getSerializableExtra(Constants.INTENT_PARAMETER_MOOD) as EMood
            moodImageView.setImageResource(mood.iconResourceId)
            moodImageView.tag = mood.toString()
        }
    }

    private fun addDiary(diaryTitle: String) {
        val currentTime = Calendar.getInstance().timeInMillis
        if (diaryType == EDiaryType.MOOD_TRACKER) {
            diary = Diary(
                diaryTitle,
                diaryType.toString(),
                moodImageView.tag.toString(),
                "",
                currentTime,
                currentTime
            )
        } else if (diaryType == EDiaryType.ULTRASOUND) {
            val fileName =
                saveImageToStorage((moodImageView.drawable as BitmapDrawable).bitmap)
            if (fileName.isEmpty()) {
                Toast.makeText(
                    activity,
                    getString(R.string.text_toast_photo_save_error),
                    Toast.LENGTH_LONG
                ).show()
                return
            } else {
                diary = Diary(
                    diaryTitle,
                    diaryType.toString(),
                    EMood.UNKNOWN.toString(),
                    fileName,
                    currentTime,
                    currentTime
                )
            }
        }

        Toast.makeText(
            context!!,
            R.string.text_toast_operation_successful,
            Toast.LENGTH_SHORT
        ).show()
        postButtonTrackerEvent("Mood Add")
        DatabaseHelper.getDatabaseHelper(context!!)!!.diaryDao().insert(diary!!)
        if (diaryType == EDiaryType.ULTRASOUND) {
            val bundle = Bundle()
            bundle.putSerializable(Constants.FRAGMENT_ARG_PARAM_DIARY, diary)
            bundle.putSerializable(
                Constants.FRAGMENT_ARG_PARAM_DIARY_TYPE,
                diaryType
            )
            findNavController().navigate(
                R.id.action_navigation_diary_details_self,
                bundle
            )
        } else {
            findNavController().navigateUp()
        }
    }

    private fun updateDiary(diaryTitle: String) {
        diary!!.title = diaryTitle
        diary!!.lastUpdateDate = Calendar.getInstance().timeInMillis
        if (diaryType == EDiaryType.MOOD_TRACKER) {
            diary!!.mood = moodImageView.tag.toString()
        } else if (diaryType == EDiaryType.ULTRASOUND) {
            val fileName =
                saveImageToStorage((moodImageView.drawable as BitmapDrawable).bitmap)
            if (fileName.isEmpty()) {
                Toast.makeText(
                    activity,
                    getString(R.string.text_toast_photo_save_error),
                    Toast.LENGTH_LONG
                ).show()
                return
            } else {
                diary!!.filePath = fileName
            }
        }
        Toast.makeText(
            context!!,
            R.string.text_toast_operation_successful,
            Toast.LENGTH_SHORT
        ).show()
        postButtonTrackerEvent("Mood Update")
        DatabaseHelper.getDatabaseHelper(context!!)!!.diaryDao().update(diary!!)
        findNavController().navigateUp()
    }

    private fun checkStoragePermission(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (!permissions.all {
                ActivityCompat.checkSelfPermission(
                    context!!,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            if (isStoragePermissionRequestFirstTime) {
                requestPermissions(
                    permissions,
                    PERMISSION_REQUEST_CODE_STORAGE
                )
            }
            return false
        }
        return true
    }

    private fun pickImageFromGallery() {
        if (!checkStoragePermission()) {
            return
        }

        val photoIntent = Intent(Intent.ACTION_GET_CONTENT)
        val mimeTypes = arrayOf("image/jpg", "image/png", "image/jpeg")
        photoIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        photoIntent.type = "image/*"
        photoIntent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(photoIntent, Constants.REQUEST_PICK_IMAGE)
    }

    private fun view3d() {
        Panorama.getInstance()
            .loadImageInfoWithPermission(
                context,
                Uri.parse(diary!!.filePath),
                PanoramaInterface.IMAGE_TYPE_SPHERICAL
            )
            .setResultCallback(
                object : ResultCallback<PanoramaInterface.ImageInfoResult?> {
                    override fun onResult(panoramaResult: PanoramaInterface.ImageInfoResult?) {
                        if (panoramaResult == null) {
                            Toast.makeText(
                                activity,
                                R.string.text_toast_unexpected_error,
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }

                        if (panoramaResult.status.isSuccess) {
                            val intent = panoramaResult.imageDisplayIntent
                            intent?.let { startActivity(it) }
                        } else {
                            Toast.makeText(
                                activity,
                                R.string.text_toast_unexpected_error,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (null != intent) {
            if (resultCode == Activity.RESULT_OK) {
                when (requestCode) {
                    Constants.REQUEST_PICK_IMAGE -> try {
                        val uri: Uri? = intent.data
                        moodImageView.setImageURI(uri)
                        moodImageView.tag = "ultrasound"
                    } catch (e: Exception) {
                        Toast.makeText(
                            activity,
                            R.string.text_toast_unexpected_error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ACTIVITY_REQUEST_CODE_CHECK_IAP -> {
                        val returnCode = IapClientHelper.parseRespCodeFromIntent(intent)
                        if (returnCode == OrderStatusCode.ORDER_STATE_SUCCESS) {
                            iapServiceControl = true
                            checkOwnedPurchases()
                        } else {
                            iapServiceControl = false
                            Toast.makeText(
                                context,
                                R.string.text_toast_unexpected_error,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    ACTIVITY_REQUEST_CODE_PURCHASE -> {
                        if (intent == null) {
                            Toast.makeText(
                                context,
                                R.string.text_toast_unexpected_error,
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }

                        val purchaseResultInfo =
                            Iap.getIapClient(context).parsePurchaseResultInfoFromIntent(intent)
                        when (purchaseResultInfo.returnCode) {
                            OrderStatusCode.ORDER_STATE_CANCEL -> {
                                Toast.makeText(
                                    context,
                                    R.string.text_toast_iap_cancel,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            OrderStatusCode.ORDER_STATE_FAILED -> {
                                Toast.makeText(
                                    context,
                                    R.string.text_toast_unexpected_error,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            OrderStatusCode.ORDER_PRODUCT_OWNED -> {
                                is3dViewFeatureOwned = true
                                view3d()
                            }
                            OrderStatusCode.ORDER_STATE_SUCCESS -> {
                                is3dViewFeatureOwned = true
                                view3d()
                            }
                            else -> {
                                Toast.makeText(
                                    context,
                                    R.string.text_toast_unexpected_error,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE_STORAGE) {
            isStoragePermissionRequestFirstTime = false
            pickImageFromGallery()
        }
    }

    private fun saveImageToStorage(bitmapImage: Bitmap): String {
        val fileName = Calendar.getInstance().timeInMillis.toString() + ".png"
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(
            MediaStore.Images.Media.RELATIVE_PATH,
            "Pictures" + "/" + "Pregnancy Tracker/Ultrasound"
        )
        values.put(MediaStore.Images.Media.IS_PENDING, true)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)

        val uri: Uri? =
            context!!.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val outputStream = context!!.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                try {
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return ""
                } finally {
                    try {
                        outputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        return ""
                    }
                }
            } else {
                return ""
            }

            values.put(MediaStore.Images.Media.IS_PENDING, false)
            context!!.contentResolver.update(uri, values, null, null)
            return uri.toString()
        } else {
            return ""
        }
    }

    private fun checkIAPService() {
        val task = Iap.getIapClient(activity).isEnvReady
        task.addOnSuccessListener {
            iapServiceControl = true
            checkOwnedPurchases()
        }.addOnFailureListener {
            if (it is IapApiException) {
                val status = it.status
                if (status.statusCode == OrderStatusCode.ORDER_HWID_NOT_LOGIN) {
                    if (status.hasResolution()) {
                        try {
                            status.startResolutionForResult(
                                activity,
                                ACTIVITY_REQUEST_CODE_CHECK_IAP
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            iapServiceControl = false
                            Toast.makeText(
                                context,
                                R.string.text_toast_unexpected_error,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else if (status.statusCode == OrderStatusCode.ORDER_ACCOUNT_AREA_NOT_SUPPORTED) {
                    iapServiceControl = false
                    Toast.makeText(
                        context,
                        R.string.text_toast_iap_area_not_supported,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun checkOwnedPurchases() {
        val ownedPurchaseReq = OwnedPurchasesReq()
        // priceType: 0: consumable; 1: non-consumable; 2: subscription
        ownedPurchaseReq.priceType = 1
        val task = Iap.getIapClient(activity).obtainOwnedPurchases(ownedPurchaseReq)
        task.addOnSuccessListener {
            if (it != null && it.inAppPurchaseDataList != null) {
                for (i in 0 until it.inAppPurchaseDataList.size) {
                    val iapData = it.inAppPurchaseDataList[i]
                    val iapSignature = it.inAppSignature[i]
                    try {
                        val inAppPurchaseDataBean = InAppPurchaseData(iapData)
                        if (inAppPurchaseDataBean.productId.equals(Constants.IAP_PRODUCT_ID_3D_VIEW)) {
                            val purchaseState = inAppPurchaseDataBean.purchaseState
                            if (purchaseState == 0) {
                                is3dViewFeatureOwned = true
                            }
                            return@addOnSuccessListener
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            context,
                            R.string.text_toast_unexpected_error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }.addOnFailureListener {
            if (it is IapApiException) {
                val status = it.status
                Toast.makeText(
                    context,
                    "${status.statusMessage} Status code: ${it.statusCode}",
                    Toast.LENGTH_LONG
                ).show()
                if (it.statusCode == 60002) {
                    iapServiceControl = false
                }
            } else {
                Toast.makeText(context, R.string.text_toast_unexpected_error, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun checkOwnedPurchasesForConsume() {
        val ownedPurchaseReq = OwnedPurchasesReq()
        // priceType: 0: consumable; 1: non-consumable; 2: subscription
        ownedPurchaseReq.priceType = 1
        val task = Iap.getIapClient(activity).obtainOwnedPurchases(ownedPurchaseReq)
        task.addOnSuccessListener {
            if (it != null && it.inAppPurchaseDataList != null) {
                for (i in 0 until it.inAppPurchaseDataList.size) {
                    val iapData = it.inAppPurchaseDataList[i]
                    val iapSignature = it.inAppSignature[i]
                    try {
                        val inAppPurchaseDataBean = InAppPurchaseData(iapData)
                        if (inAppPurchaseDataBean.productId.equals(Constants.IAP_PRODUCT_ID_3D_VIEW)) {
                            if (consumeOwnedPurchases(iapData, iapSignature)) {
                                Toast.makeText(
                                    context,
                                    R.string.text_toast_iap_consume_successful,
                                    Toast.LENGTH_LONG
                                ).show()
                                is3dViewFeatureOwned = false
                            } else {
                                Toast.makeText(
                                    context,
                                    R.string.text_toast_unexpected_error,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            return@addOnSuccessListener
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            context,
                            R.string.text_toast_unexpected_error,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }.addOnFailureListener {
            if (it is IapApiException) {
                val status = it.status
                Toast.makeText(
                    context,
                    "${status.statusMessage} Status code: ${it.statusCode}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(context, R.string.text_toast_unexpected_error, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun openPurchasePage() {
        val purchasesRequest = PurchaseIntentReq()
        purchasesRequest.productId = Constants.IAP_PRODUCT_ID_3D_VIEW
        // priceType: 0: consumable; 1: non-consumable; 2: subscription
        purchasesRequest.priceType = 1
        purchasesRequest.developerPayload = "test"

        val task = Iap.getIapClient(activity).createPurchaseIntent(purchasesRequest)
        task.addOnSuccessListener {
            val status = it.status
            if (status.hasResolution()) {
                try {
                    status.startResolutionForResult(
                        activity,
                        ACTIVITY_REQUEST_CODE_PURCHASE
                    )
                } catch (e: IntentSender.SendIntentException) {
                    Toast.makeText(
                        context,
                        R.string.text_toast_unexpected_error,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.addOnFailureListener {
            if (it is IapApiException) {
                val status = it.status
                Toast.makeText(
                    context,
                    "${status.statusMessage} Status code: ${it.statusCode}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(context, R.string.text_toast_unexpected_error, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun consumeOwnedPurchases(
        inAppPurchaseData: String,
        inAppPurchaseDataSignature: String
    ): Boolean {
        if (inAppPurchaseDataSignature.isBlank()) return false
        if (inAppPurchaseData.isBlank()) return false

        val securityHelper = SecurityHelper()
        if (!securityHelper.doCheck(inAppPurchaseData, inAppPurchaseDataSignature)) {
            Log.d(TAG, "Sign fail")
            return false
        }

        val purchaseToken = InAppPurchaseData(inAppPurchaseData).purchaseToken
        when (purchaseToken == null) {
            true -> {
                Log.d(TAG, "purchase token is null")
                return false
            }
            false -> {
                Iap.getIapClient(activity)
                    .consumeOwnedPurchase(createConsumeOwnedPurchaseRequest(purchaseToken))
                    .addOnSuccessListener {
                        Log.d(TAG, "consumeOwnedPurchase success")
                        return@addOnSuccessListener
                    }.addOnFailureListener {
                        Log.d(TAG, it.toString())
                        return@addOnFailureListener
                    }
            }
        }
        return true
    }

    private fun createConsumeOwnedPurchaseRequest(purchaseToken: String): ConsumeOwnedPurchaseReq {
        val consumeOwnedPurchaseRequest = ConsumeOwnedPurchaseReq()
        consumeOwnedPurchaseRequest.purchaseToken = purchaseToken
        return consumeOwnedPurchaseRequest
    }
}