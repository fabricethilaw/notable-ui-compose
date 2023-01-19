package com.example.sample.ui.autosizetext

import androidx.annotation.RestrictTo
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.core.widget.TextViewCompat

/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Interface which allows a [Text] to receive background auto-sizing calls
 * from [TextViewCompat] when running on API v26 devices or lower.
 *
 * @hide Internal use only
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
interface AutoSizeableText {
    /**
     * Specify whether this widget should automatically scale the text to try to perfectly fit
     * within the layout bounds by using the default auto-size configuration.
     *
     * @param autoSizeTextType the type of auto-size. Must be one of
     * [AUTO_SIZE_TEXT_TYPE_NONE] or
     * [AUTO_SIZE_TEXT_TYPE_UNIFORM]
     *
     */
    @Composable
    fun SetAutoSizeTextTypeWithDefaults(
        @AutoSizeTextType autoSizeTextType: Int,
        text: AnnotatedString,
        textStyle: TextStyle,
        constraints: Constraints,
        autoTextSizeResult: (originalTextSize: TextUnit, optimalTextSize: TextUnit) -> Unit
    )

    /**
     * Specify whether this widget should automatically scale the text to try to perfectly fit
     * within the layout bounds. If all the configuration params are valid the type of auto-size is
     * set to [TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM].
     *
     * @param autoSizeMinTextSize the minimum text size available for auto-size
     * @param autoSizeMaxTextSize the maximum text size available for auto-size
     * @param autoSizeStepGranularity the auto-size step granularity. It is used in conjunction with
     * the minimum and maximum text size in order to build the set of
     * text sizes the system uses to choose from when auto-sizing
     *
     */
    @Composable
    fun SetAutoSizeTextTypeUniformWithConfiguration(
        autoSizeMinTextSize: TextUnit,
        autoSizeMaxTextSize: TextUnit,
        autoSizeStepGranularity: TextUnit,
        text: AnnotatedString,
        textStyle: TextStyle,
        constraints: Constraints,
        autoTextSizeResult: (originalTextSize: TextUnit, optimalTextSize: TextUnit) -> Unit
    )

    /**
     * Specify whether this widget should automatically scale the text to try to perfectly fit
     * within the layout bounds. If at least one value from the `presetSizes` is valid
     * then the type of auto-size is set to [AUTO_SIZE_TEXT_TYPE_UNIFORM].
     *
     * @param presetSizes an `Int` array list of sizes in text units.
     *
     */
    @Composable
    fun SetAutoSizeTextTypeUniformWithPresetSizes(
        presetSizes: ArrayList<TextUnit>, text: AnnotatedString,
        textStyle: TextStyle,
        constraints: Constraints,
        autoTextSizeResult: (originalTextSize: TextUnit, optimalTextSize: TextUnit) -> Unit
    )

}