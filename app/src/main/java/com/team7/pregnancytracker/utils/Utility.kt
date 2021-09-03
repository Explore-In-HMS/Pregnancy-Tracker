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

package com.huawei.hms.image.vision.sample

import android.content.Context

import android.util.Log

import java.io.*


object Utility {
    private const val TAG = "Utility"

    fun copyAssetsFileToDirs(
        context: Context,
        oldPath: String,
        newPath: String
    ): Boolean {
        val inputStream: InputStream? = null
        val outputStream: FileOutputStream? = null
        try {
            val fileNames = context.assets.list(oldPath)
            if (fileNames!!.size > 0) {
                val file = File(newPath)
                file.mkdirs()
                for (fileName in fileNames) {
                    copyAssetsFileToDirs(
                        context,
                        "$oldPath/$fileName",
                        "$newPath/$fileName"
                    )
                }
            } else {
                val `is` = context.assets.open(oldPath)
                val fos = FileOutputStream(File(newPath))
                val buffer = ByteArray(1024)
                var byteCount = 0
                while (`is`.read(buffer).also { byteCount = it } != -1) {
                    fos.write(buffer, 0, byteCount)
                }
                fos.flush()
                `is`.close()
                fos.close()
            }
        } catch (e: IOException) {
            Log.e(TAG, e.message)
            return false
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                Log.e(TAG, e.message)
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close()
                    } catch (e: IOException) {
                        Log.e(
                            TAG,
                            e.message
                        )
                    }
                }
            }
        }
        return true
    }
}