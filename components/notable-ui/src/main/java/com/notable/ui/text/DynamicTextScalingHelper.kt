package com.notable.ui.text

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import kotlin.math.floor
import kotlin.math.roundToInt

internal class DynamicTextScalingHelper : AutoSizeableText {

  // Auto-size text type.
  private var mAutoScalingType: AutoScalingType = AutoScalingType.Uniform

  // Specify if auto-size text is needed.
  private var mNeedsAutoSizeText: Boolean = false

  // Step size for auto-sizing in pixels.
  private var mAutoSizeStepGranularityInPx: Float =
    UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE

  // Minimum text size for auto-sizing in pixels.
  private var mAutoSizeMinTextSizeInPx: Float =
    UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE

  // Maximum text size for auto-sizing in pixels.
  private var mAutoSizeMaxTextSizeInPx: Float =
    UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE

  // Contains a (specified or computed) distinct sorted set of text sizes in pixels to pick from
  // when auto-sizing text.
  private var mAutoSizeTextSizesInPx: Array<Int> = emptyArray()

  // Specifies whether auto-size should use the provided auto size steps set or if it should
  // build the steps set using mAutoSizeMinTextSizeInPx, mAutoSizeMaxTextSizeInPx and
  // mAutoSizeStepGranularityInPx.
  private var mHasPresetAutoSizeValues: Boolean = false

  /**
   * Specify whether this widget should automatically scale the text to try to perfectly fit
   * within the layout bounds by using the default auto-size configuration.
   */
  @OptIn(ExperimentalTextApi::class)
  override fun setUniformScalingOptionWithDefaults(
    density: Density,
    textMeasurer: TextMeasurer,
    autoScalingType: AutoScalingType,
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int,
    constraints: Constraints,
  ): Pair<TextUnit, TextUnit> {
    if (supportsAutoSizeText()) {
      when (autoScalingType) {
        AutoScalingType.None -> {
          clearAutoSizeConfiguration()
          return Pair(textStyle.fontSize, textStyle.fontSize)
        }
        AutoScalingType.Uniform -> {
          val autoSizeMinTextSizeInPx = with(density) {
            DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP.toPx()
          }
          val autoSizeMaxTextSizeInPx = with(density) {
            DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP.toPx()
          }

          validateAndSetAutoSizeTextTypeUniformConfiguration(
            autoSizeMinTextSizeInPx,
            autoSizeMaxTextSizeInPx,
            DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX.toFloat()
          )
          return if (setupAutoSizeText()) {
            autoSizeText(density, textMeasurer, text, textStyle, maxLines, constraints)
          } else {
            Pair(textStyle.fontSize, textStyle.fontSize)
          }
        }
      }
    } else {
      return Pair(textStyle.fontSize, textStyle.fontSize)
    }
  }


  /**
   * Specify whether this widget should automatically scale the text to try to perfectly fit
   * within the layout bounds. If all the configuration params are valid the type of auto-size is
   * set to [.AUTO_SIZE_TEXT_TYPE_UNIFORM].
   *
   * @param autoSizeMinTextSize the minimum text size available for auto-size
   * @param autoSizeMaxTextSize the maximum text size available for auto-size
   * @param autoSizeStepGranularity the auto-size step granularity. It is used in conjunction with
   * the minimum and maximum text size in order to build the set of
   * text sizes the system uses to choose from when auto-sizing.
   *
   * @throws IllegalArgumentException if any of the configuration params are invalid.
   */
  @OptIn(ExperimentalTextApi::class)
  override fun setScalingWithConfiguration(
    density: Density,
    textMeasurer: TextMeasurer,
    autoSizeMinTextSize: TextUnit,
    autoSizeMaxTextSize: TextUnit,
    autoSizeStepGranularity: TextUnit,
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int,
    constraints: Constraints,
  ): Pair<TextUnit, TextUnit> {
    if (supportsAutoSizeText()) {
      val autoSizeMinTextSizeInPx = with(density) {
        autoSizeMinTextSize.toPx()
      }
      val autoSizeMaxTextSizeInPx = with(density) {
        autoSizeMaxTextSize.toPx()
      }
      val autoSizeStepGranularityInPx = with(density) {
        autoSizeStepGranularity.toPx()
      }
      validateAndSetAutoSizeTextTypeUniformConfiguration(
        autoSizeMinTextSizeInPx,
        autoSizeMaxTextSizeInPx,
        autoSizeStepGranularityInPx
      )
      return if (setupAutoSizeText()) {
        autoSizeText(
          density,
          textMeasurer,
          text,
          textStyle,
          maxLines = maxLines,
          constraints = constraints
        )
      } else {
        Pair(textStyle.fontSize, textStyle.fontSize)
      }
    } else {
      return Pair(textStyle.fontSize, textStyle.fontSize)
    }
  }


  @OptIn(ExperimentalTextApi::class)
  override fun setScalingWithPresetSizes(
    density: Density,
    textMeasurer: TextMeasurer,
    presetSizes: Array<TextUnit>,
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int,
    constraints: Constraints,
  ): Pair<TextUnit, TextUnit> {
    if (supportsAutoSizeText()) {
      val presetSizesLength = presetSizes.size
      if (presetSizesLength > 0) {
        val presetSizesInPx = IntArray(presetSizesLength)
        // Convert all to sizes to pixels.
        with(density) {
          for (i in 0 until presetSizesLength) {
            presetSizesInPx[i] = presetSizes[i].toPx().roundToInt()
          }
        }

        mAutoSizeTextSizesInPx =
          cleanupAutoSizePresetSizes(presetSizesInPx.toTypedArray())
        require(setupAutoSizeUniformPresetSizesConfiguration()) {
          ("None of the preset sizes is valid: "
            + presetSizes.toString())
        }
      } else {
        mHasPresetAutoSizeValues = false
      }
      return if (setupAutoSizeText()) {
        autoSizeText(density, textMeasurer, text, textStyle, maxLines, constraints)
      } else {
        Pair(textStyle.fontSize, textStyle.fontSize)
      }
    } else {
      return Pair(textStyle.fontSize, textStyle.fontSize)
    }
  }

  /**
   * If all params are valid then save the auto-size configuration.
   */
  private fun validateAndSetAutoSizeTextTypeUniformConfiguration(
    autoSizeMinTextSizeInPx: Float,
    autoSizeMaxTextSizeInPx: Float,
    autoSizeStepGranularityInPx: Float
  ) {
    // First validate.
    if (autoSizeMinTextSizeInPx <= 0) {
      throw java.lang.IllegalArgumentException(
        "Minimum auto-size text size ("
          + autoSizeMinTextSizeInPx + "px) is less or equal to (0px)"
      )
    }
    if (autoSizeMaxTextSizeInPx <= autoSizeMinTextSizeInPx) {
      throw java.lang.IllegalArgumentException(
        ("Maximum auto-size text size ("
          + autoSizeMaxTextSizeInPx + "px) is less or equal to minimum auto-size "
          + "text size (" + autoSizeMinTextSizeInPx + "px)")
      )
    }
    if (autoSizeStepGranularityInPx <= 0) {
      throw java.lang.IllegalArgumentException(
        ("The auto-size step granularity ("
          + autoSizeStepGranularityInPx + "px) is less or equal to (0px)")
      )
    }

    // All good, persist the configuration.
    mAutoScalingType = AutoScalingType.Uniform
    mAutoSizeMinTextSizeInPx = autoSizeMinTextSizeInPx
    mAutoSizeMaxTextSizeInPx = autoSizeMaxTextSizeInPx
    mAutoSizeStepGranularityInPx = autoSizeStepGranularityInPx
    mHasPresetAutoSizeValues = false
  }

  private fun setupAutoSizeText(): Boolean {
    if (supportsAutoSizeText() && mAutoScalingType == AutoScalingType.Uniform) {
      // Calculate the sizes set based on minimum size, maximum size and step size if we do
      // not have a predefined set of sizes or if the current sizes array is empty.
      if (!mHasPresetAutoSizeValues || mAutoSizeTextSizesInPx.isEmpty()) {
        val autoSizeValuesLength = floor(
          ((mAutoSizeMaxTextSizeInPx
            - mAutoSizeMinTextSizeInPx) / mAutoSizeStepGranularityInPx).toDouble()
        ).toInt() + 1
        val autoSizeTextSizesInPx = IntArray(autoSizeValuesLength)
        for (i in 0 until autoSizeValuesLength) {
          autoSizeTextSizesInPx[i] =
            (mAutoSizeMinTextSizeInPx + i * mAutoSizeStepGranularityInPx).roundToInt()
        }
        mAutoSizeTextSizesInPx =
          cleanupAutoSizePresetSizes(autoSizeTextSizesInPx.toTypedArray())
      }
      mNeedsAutoSizeText = true
    } else {
      mNeedsAutoSizeText = false
    }
    return mNeedsAutoSizeText
  }

  private fun setupAutoSizeUniformPresetSizesConfiguration(): Boolean {
    val sizesLength = mAutoSizeTextSizesInPx.size
    mHasPresetAutoSizeValues = sizesLength > 0
    if (mHasPresetAutoSizeValues) {
      mAutoScalingType = AutoScalingType.Uniform
      mAutoSizeMinTextSizeInPx = mAutoSizeTextSizesInPx[0].toFloat()
      mAutoSizeMaxTextSizeInPx = mAutoSizeTextSizesInPx[sizesLength - 1].toFloat()
      mAutoSizeStepGranularityInPx = UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE
    }
    return mHasPresetAutoSizeValues
  }

  private fun clearAutoSizeConfiguration() {
    mAutoScalingType = AutoScalingType.None
    mAutoSizeMinTextSizeInPx = UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE
    mAutoSizeMaxTextSizeInPx = UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE
    mAutoSizeStepGranularityInPx = UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE
    mAutoSizeTextSizesInPx = emptyArray()
    mNeedsAutoSizeText = false
  }

  // Returns distinct sorted positive values.
  private fun cleanupAutoSizePresetSizes(presetValues: Array<Int>): Array<Int> {
    val presetValuesLength = presetValues.size
    if (presetValuesLength == 0) {
      return presetValues
    }

    presetValues.sort()
    val uniqueValidSizes = arrayListOf<Int>()
    for (i in 0 until presetValuesLength) {
      val currentPresetValue = presetValues[i]
      if (currentPresetValue > 0
        && uniqueValidSizes.binarySearch(currentPresetValue) < 0
      ) {
        uniqueValidSizes.add(currentPresetValue)
      }
    }
    return if (presetValuesLength == uniqueValidSizes.size) presetValues else uniqueValidSizes.toTypedArray()
  }

  @OptIn(ExperimentalTextApi::class)
  private fun autoSizeText(
    density: Density,
    textMeasurer: TextMeasurer,
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int,
    constraints: Constraints
  ): Pair<TextUnit, TextUnit> {
    return if (mNeedsAutoSizeText) {
      val optimalTextSizeValue: Int =
        findLargestTextSizeWhichFits(
          density,
          textMeasurer,
          text,
          textStyle,
          maxLines = maxLines,
          constraints = constraints
        )
      val optimalTextSize = with(density) { optimalTextSizeValue.toSp() }
      Pair(textStyle.fontSize, optimalTextSize)
    } else {
      // Always try to auto-size if enabled. Functions that do not want to trigger auto-sizing
      // after the next layout pass should set this to false.
      mNeedsAutoSizeText = true
      Pair(textStyle.fontSize, textStyle.fontSize)
    }
  }

  /**
   * Performs a binary search to find the largest text size that will still fit within the size
   * available to this layout.
   */
  @OptIn(ExperimentalTextApi::class)
  private fun findLargestTextSizeWhichFits(
    density: Density,
    textMeasurer: TextMeasurer,
    text: AnnotatedString,
    textStyle: TextStyle,
    maxLines: Int,
    constraints: Constraints
  ): Int {
    val sizesCount = mAutoSizeTextSizesInPx.size
    check(sizesCount != 0) { "No available text sizes to choose from." }
    var bestSizeIndex = 0
    var lowIndex = 1
    var highIndex = sizesCount - 1
    var sizeToTryIndex: Int

    while (lowIndex <= highIndex) {
      sizeToTryIndex = (lowIndex + highIndex) / 2
      val suggestedFontSize: TextUnit =
        with(density) {
          mAutoSizeTextSizesInPx[sizeToTryIndex].toSp()
        }
      if (suggestedSizeFitsInSpace(
          textMeasurer,
          text = text,
          style = textStyle,
          maxLines = maxLines,
          suggestedFontSize = suggestedFontSize,
          constraints = constraints
        )
      ) {
        bestSizeIndex = lowIndex
        lowIndex = sizeToTryIndex + 1
      } else {
        highIndex = sizeToTryIndex - 1
        bestSizeIndex = highIndex
      }
    }
    return mAutoSizeTextSizesInPx[bestSizeIndex]
  }

  @OptIn(ExperimentalTextApi::class)
  private fun suggestedSizeFitsInSpace(
    textMeasurer: TextMeasurer,
    text: AnnotatedString,
    maxLines: Int,
    style: TextStyle,
    constraints: Constraints,
    suggestedFontSize: TextUnit
  ): Boolean {
    val textLayoutResult = textMeasurer.measure(
      text = text,
      style = style.copy(fontSize = suggestedFontSize, color = Color.Yellow),
      overflow = TextOverflow.Ellipsis,
      maxLines = maxLines,
      softWrap = true,
      constraints = constraints
    )
    val overflow = textLayoutResult.hasVisualOverflow
    return !overflow
  }

  private fun supportsAutoSizeText() = true

  private companion object {
    // Default minimum size for auto-sizing text in scaled pixels.
    private val DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP: TextUnit = 12.sp

    // Default maximum size for auto-sizing text in scaled pixels.
    private val DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP: TextUnit = 112.sp

    // Default value for the step size in pixels.
    private const val DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX: Int = 1

    // Use this to specify that any of the auto-size configuration int values have not been set.
    private const val UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE: Float = -1f
  }
}