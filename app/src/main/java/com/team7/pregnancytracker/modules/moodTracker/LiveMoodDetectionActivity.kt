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

package com.team7.pregnancytracker.modules.moodTracker

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting
import com.team7.pregnancytracker.R
import com.team7.pregnancytracker.helpers.postMoodDetectorEvent
import com.team7.pregnancytracker.helpers.postMoodTrackerEvent
import com.team7.pregnancytracker.utils.Constants
import java.io.IOException

class LiveMoodDetectionActivity : AppCompatActivity() {

    //Variables
    private val TAG = LiveMoodDetectionActivity::class.java.simpleName
    private lateinit var mlFaceAnalyzer: MLFaceAnalyzer
    private var lensEngine: LensEngine? = null
    private var lensType = LensEngine.BACK_LENS

    //Widgets
    private lateinit var cameraSourcePreview: CameraSourcePreview
    private lateinit var graphicOverlay: GraphicOverlay
    private lateinit var moodImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_mood_detection)

        cameraSourcePreview = findViewById(R.id.camera_preview)
        graphicOverlay = findViewById(R.id.graphic_overlay)
        moodImageView = findViewById(R.id.image_view_mood)

        createFaceAnalyzer()

        val cameraToggleButton = findViewById<ToggleButton>(R.id.toggle_button_camera)
        cameraToggleButton.setOnCheckedChangeListener { compoundButton, b ->
            if (lensEngine != null) {
                if (b) {
                    lensType = LensEngine.FRONT_LENS
                } else {
                    lensType = LensEngine.BACK_LENS
                }
            }
            lensEngine!!.close()
            createLensEngine()
            startLensEngine()
        }

        val takeMoodCarView = findViewById<CardView>(R.id.card_view_take_mood)
        takeMoodCarView.setOnClickListener {
            if (moodImageView.tag != null) {
                val mood = EMood.valueOf(moodImageView.tag.toString())
                postMoodTrackerEvent(mood)
                val intent = Intent()
                intent.action = Constants.INTENT_ACTION_MOOD_RECEIVED
                intent.putExtra(Constants.INTENT_PARAMETER_MOOD, mood)
                sendBroadcast(intent)
                finish()
            } else {
                finish()
            }
        }

        checkCameraPermission()
    }

    override fun onResume() {
        super.onResume()
        startLensEngine()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (lensEngine != null) {
            lensEngine!!.release()
        }
        if (mlFaceAnalyzer != null) {
            mlFaceAnalyzer.destroy()
        }
    }

    override fun onPause() {
        super.onPause()
        cameraSourcePreview.stop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.PERMISSION_REQUEST_CODE_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                createLensEngine()
            }
        }
    }

    private fun checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            createLensEngine()
        } else {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)

        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            )
        ) {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                Constants.PERMISSION_REQUEST_CODE_CAMERA
            )
            return
        }
    }

    private fun createFaceAnalyzer(): MLFaceAnalyzer {
        val settings = MLFaceAnalyzerSetting.Factory()
            .setFeatureType(MLFaceAnalyzerSetting.TYPE_FEATURES)
            .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
            .allowTracing()
            .create()
        mlFaceAnalyzer = MLAnalyzerFactory.getInstance().getFaceAnalyzer(settings)
        mlFaceAnalyzer.setTransactor(
            FaceAnalyzerTransactor(
                graphicOverlay,
                object : IMoodChangeListener {
                    override fun onMoodChange(mood: EMood) {
                        if (moodImageView.tag == null) {
                            postMoodDetectorEvent(mood)
                        } else {
                            if (moodImageView.tag.toString() != mood.toString()) {
                                postMoodDetectorEvent(mood)
                            }
                        }
                        moodImageView.tag = mood.toString()
                        moodImageView.setImageResource(mood.iconResourceId)
                    }
                })
        )
        return mlFaceAnalyzer
    }

    private fun createLensEngine() {
        lensEngine = LensEngine.Creator(this, mlFaceAnalyzer)
            .setLensType(lensType)
            .applyDisplayDimension(1600, 1024)
            .applyFps(25.0f)
            .enableAutomaticFocus(true)
            .create()
    }

    private fun startLensEngine() {
        if (lensEngine != null) {
            try {
                cameraSourcePreview.start(lensEngine, graphicOverlay)
            } catch (e: IOException) {
                lensEngine!!.release()
                lensEngine = null
            }
        }
    }
}