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

import com.team7.pregnancytracker.R
import java.io.Serializable

enum class EMood(var iconResourceId: Int) : Serializable {
    UNKNOWN(R.drawable.icon_mood_tracker),
    SMILING(R.drawable.icon_mood_smile),
    NEUTRAL(R.drawable.icon_mood_neutral),
    ANGRY(R.drawable.icon_mood_angry),
    FEAR(R.drawable.icon_mood_fear),
    SAD(R.drawable.icon_mood_sad),
    DISGUST(R.drawable.icon_mood_disgust),
    SURPRISE(R.drawable.icon_mood_suprise),
}