package com.notable.ui.text

import androidx.annotation.RestrictTo
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit

/*
 * Copyright (C) 2023 Thilaw Fabrice KIKI
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
 * Interface which describes background auto-sizing calls
 * from [AutoSizeTextHelper] when [AutoSizeText] is about to render a text.
 *
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
internal interface AutoSizeableText {
  /**
   * Specify whether this widget should automatically scale the text to try to perfectly fit
   * within the layout bounds by using the default auto-size configuration.
   *
   * @param autoSizeTextType the type of auto-size. Must be one of
   * [AutoSizeTextType.Uniform] or
   * [AutoSizeTextType.None]
   *
   */
  @OptIn(ExperimentalTextApi::class)
  fun setAutoSizeTextTypeWithDefaults(
    density: Density,
    textMeasurer: TextMeasurer,
    autoSizeTextType: AutoSizeTextType,
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int,
    constraints: Constraints,
  ): Pair<TextUnit, TextUnit>

  /**
   * Specify whether this widget should automatically scale the text to try to perfectly fit
   * within the layout bounds. If all the configuration params are valid the type of auto-size is
   * set to [AutoSizeTextType.Uniform].
   *
   * @param autoSizeMinTextSize the minimum text size available for auto-size
   * @param autoSizeMaxTextSize the maximum text size available for auto-size
   * @param autoSizeStepGranularity the auto-size step granularity. It is used in conjunction with
   * the minimum and maximum text size in order to build the set of
   * text sizes the system uses to choose from when auto-sizing
   *
   */
  @OptIn(ExperimentalTextApi::class)
  fun setAutoSizeTextTypeUniformWithConfiguration(
    density: Density,
    textMeasurer: TextMeasurer,
    autoSizeMinTextSize: TextUnit,
    autoSizeMaxTextSize: TextUnit,
    autoSizeStepGranularity: TextUnit,
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int,
    constraints: Constraints
  ): Pair<TextUnit, TextUnit>

  /**
   * Specify whether this widget should automatically scale the text to try to perfectly fit
   * within the layout bounds. If at least one value from the `presetSizes` is valid
   * then the type of auto-size is set to [AutoSizeTextType.Uniform].
   *
   * @param presetSizes an array list of sizes in text units.
   *
   */
  @OptIn(ExperimentalTextApi::class)
  fun setAutoSizeTextTypeUniformWithPresetSizes(
    density: Density,
    textMeasurer: TextMeasurer,
    presetSizes: Array<TextUnit>,
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int,
    constraints: Constraints
  ): Pair<TextUnit, TextUnit>
}

internal sealed interface AutoSizeTextType {
  object Uniform : AutoSizeTextType
  object None : AutoSizeTextType
}