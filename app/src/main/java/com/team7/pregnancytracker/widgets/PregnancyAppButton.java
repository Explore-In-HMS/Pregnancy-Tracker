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

package com.team7.pregnancytracker.widgets;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;

import com.team7.pregnancytracker.PregnancyApp;

public class PregnancyAppButton extends AppCompatButton {

    public PregnancyAppButton(Context context) {
        this(context, null);
        this.setTypeface(PregnancyApp.Companion.getPregnancyAppFont());
    }

    public PregnancyAppButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.setTypeface(PregnancyApp.Companion.getPregnancyAppFont());
    }

    public PregnancyAppButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(PregnancyApp.Companion.getPregnancyAppFont());
    }
}
