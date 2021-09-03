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

import android.util.Base64
import com.team7.pregnancytracker.utils.Constants
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

class SecurityHelper {
    fun doCheck(
        content: String,
        sign: String
    ): Boolean {
        if (Constants.IAP_PUBLIC_KEY.isBlank()) return false

        val keyFactory =
            KeyFactory.getInstance("RSA")

        val encodedKey =
            Base64.decode(Constants.IAP_PUBLIC_KEY, Base64.DEFAULT)

        val publicKey =
            keyFactory.generatePublic(X509EncodedKeySpec(encodedKey))

        val signature =
            Signature.getInstance(Constants.IAP_KEY_ALGORITHMS)

        signature.initVerify(publicKey)
        signature.update(content.toByteArray(charset("utf-8")))

        return signature.verify(Base64.decode(sign, Base64.DEFAULT))
    }
}