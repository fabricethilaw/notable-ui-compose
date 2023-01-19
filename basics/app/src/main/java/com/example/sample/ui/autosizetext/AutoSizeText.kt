package com.example.sample.ui.autosizetext

import androidx.annotation.IntDef
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToInt

const val AUTO_SIZE_TEXT_TYPE_NONE = 0

/**
 * The TextView scales text size both horizontally and vertically to fit within the
 * container.
 */
const val AUTO_SIZE_TEXT_TYPE_UNIFORM = 1

/** @hide
 */
@IntDef(
    // prefix = ["AUTO_SIZE_TEXT_TYPE_"],
    value = [AUTO_SIZE_TEXT_TYPE_NONE, AUTO_SIZE_TEXT_TYPE_UNIFORM]
)
@Retention(AnnotationRetention.SOURCE)
annotation class AutoSizeTextType


internal class AutoSizeTextHelper : AutoSizeableText {

    // Auto-size text type.
    private var mAutoSizeTextType: Int = AUTO_SIZE_TEXT_TYPE_NONE

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
     *
     * @param autoSizeTextType the type of auto-size. Must be one of
     * [AUTO_SIZE_TEXT_TYPE_NONE] or
     * [AUTO_SIZE_TEXT_TYPE_UNIFORM]
     *
     * @throws IllegalArgumentException if `autoSizeTextType` is none of the types above.
     *
     * @attr ref android.R.styleable#TextView_autoSizeTextType
     *
     * @see .getAutoSizeTextType
     */
    @Composable
    override fun SetAutoSizeTextTypeWithDefaults(
        @AutoSizeTextType autoSizeTextType: Int,
        text: AnnotatedString,
        textStyle: TextStyle,
        constraints: Constraints,
        autoTextSizeResult: (originalTextSize: TextUnit, optimalTextSize: TextUnit) -> Unit
    ) {
        if (supportsAutoSizeText()) {
            when (autoSizeTextType) {
                AUTO_SIZE_TEXT_TYPE_NONE -> clearAutoSizeConfiguration()
                AUTO_SIZE_TEXT_TYPE_UNIFORM -> {
                    val autoSizeMinTextSizeInPx = with(LocalDensity.current) {
                        DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP.toPx()
                    }
                    val autoSizeMaxTextSizeInPx = with(LocalDensity.current) {
                        DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP.toPx()
                    }

                    validateAndSetAutoSizeTextTypeUniformConfiguration(
                        autoSizeMinTextSizeInPx,
                        autoSizeMaxTextSizeInPx,
                        DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX.toFloat()
                    )
                    if (setupAutoSizeText()) {
                        AutoSizeText(text, textStyle, constraints, autoTextSizeResult)
                    }
                }
                else -> throw IllegalArgumentException(
                    "Unknown auto-size text type: $autoSizeTextType"
                )
            }
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
    @Composable
    override fun SetAutoSizeTextTypeUniformWithConfiguration(
        autoSizeMinTextSize: TextUnit,
        autoSizeMaxTextSize: TextUnit,
        autoSizeStepGranularity: TextUnit,
        text: AnnotatedString,
        textStyle: TextStyle,
        constraints: Constraints,
        autoTextSizeResult: (originalTextSize: TextUnit, optimalTextSize: TextUnit) -> Unit
    ) {
        if (supportsAutoSizeText()) {
            val autoSizeMinTextSizeInPx = with(LocalDensity.current) {
                autoSizeMinTextSize.toPx()
            }
            val autoSizeMaxTextSizeInPx = with(LocalDensity.current) {
                autoSizeMaxTextSize.toPx()
            }
            val autoSizeStepGranularityInPx = with(LocalDensity.current) {
                autoSizeStepGranularity.toPx()
            }
            validateAndSetAutoSizeTextTypeUniformConfiguration(
                autoSizeMinTextSizeInPx,
                autoSizeMaxTextSizeInPx,
                autoSizeStepGranularityInPx
            )
            if (setupAutoSizeText()) {
                AutoSizeText(text, textStyle, constraints, autoTextSizeResult)
            }
        }
    }


    @Composable
    override fun SetAutoSizeTextTypeUniformWithPresetSizes(
        presetSizes: ArrayList<TextUnit>,
        text: AnnotatedString,
        textStyle: TextStyle,
        constraints: Constraints,
        autoTextSizeResult: (originalTextSize: TextUnit, optimalTextSize: TextUnit) -> Unit
    ) {
        if (supportsAutoSizeText()) {
            val presetSizesLength = presetSizes.size
            if (presetSizesLength > 0) {
                val presetSizesInPx = IntArray(presetSizesLength)
                // Convert all to sizes to pixels.
                with(LocalDensity.current) {
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
            if (setupAutoSizeText()) {
                AutoSizeText(text, textStyle, constraints, autoTextSizeResult)
            }
        }
    }

    @Composable
    private fun SetupAutoSizeUniformPresetSizes(textSizes: Array<TextUnit>) {
        val textSizesLength = textSizes.size
        val parsedSizes = IntArray(textSizesLength)
        if (textSizesLength > 0) {
            val density = LocalDensity.current
            for (i in 0 until textSizesLength) {
                parsedSizes[i] = with(density) { textSizes[i].toPx().roundToInt() }
            }
            mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(parsedSizes.toTypedArray())
            setupAutoSizeUniformPresetSizesConfiguration()
        }
    }

    /**
     * If all params are valid then save the auto-size configuration.
     *
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
        mAutoSizeTextType = AUTO_SIZE_TEXT_TYPE_UNIFORM
        mAutoSizeMinTextSizeInPx = autoSizeMinTextSizeInPx
        mAutoSizeMaxTextSizeInPx = autoSizeMaxTextSizeInPx
        mAutoSizeStepGranularityInPx = autoSizeStepGranularityInPx
        mHasPresetAutoSizeValues = false
    }

    private fun setupAutoSizeText(): Boolean {
        if (supportsAutoSizeText() && mAutoSizeTextType == AUTO_SIZE_TEXT_TYPE_UNIFORM) {
            // Calculate the sizes set based on minimum size, maximum size and step size if we do
            // not have a predefined set of sizes or if the current sizes array is empty.
            if (!mHasPresetAutoSizeValues || mAutoSizeTextSizesInPx.size == 0) {
                val autoSizeValuesLength = floor(
                    ((mAutoSizeMaxTextSizeInPx
                            - mAutoSizeMinTextSizeInPx) / mAutoSizeStepGranularityInPx).toDouble()
                ).toInt() + 1
                val autoSizeTextSizesInPx = arrayOf(autoSizeValuesLength)
                for (i in 0 until autoSizeValuesLength) {
                    autoSizeTextSizesInPx[i] =
                        (mAutoSizeMinTextSizeInPx + i * mAutoSizeStepGranularityInPx).roundToInt()
                }
                mAutoSizeTextSizesInPx =
                    cleanupAutoSizePresetSizes(autoSizeTextSizesInPx)
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
            mAutoSizeTextType = AUTO_SIZE_TEXT_TYPE_UNIFORM
            mAutoSizeMinTextSizeInPx = mAutoSizeTextSizesInPx[0].toFloat()
            mAutoSizeMaxTextSizeInPx = mAutoSizeTextSizesInPx[sizesLength - 1].toFloat()
            mAutoSizeStepGranularityInPx = UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE
        }
        return mHasPresetAutoSizeValues
    }

    private fun clearAutoSizeConfiguration() {
        mAutoSizeTextType = AUTO_SIZE_TEXT_TYPE_NONE
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


    @Composable
    private fun AutoSizeText(
        text: AnnotatedString,
        textStyle: TextStyle,
        constraints: Constraints,
        autoTextSizeResult: (originalTextSize: TextUnit, optimalTextSize: TextUnit) -> Unit
    ) {
/*
        if (!isAutoSizeEnabled()) {
            return
        }
*/
        if (mNeedsAutoSizeText) {
/*
            if (constraints.maxWidth <= 0 || constraints.maxHeight <= 0) {
                autoSizeResult.invoke(textStyle.fontSize, 0.sp)
                return
            }
*/
            val optimalTextSizeValue: Int =
                findLargestTextSizeWhichFits(text, textStyle, constraints)
            val optimalTextSize = with(LocalDensity.current) { optimalTextSizeValue.toSp() }
            autoTextSizeResult.invoke(textStyle.fontSize, optimalTextSize)
        }
        // Always try to auto-size if enabled. Functions that do not want to trigger auto-sizing
        // after the next layout pass should set this to false.
        mNeedsAutoSizeText = true
    }

    /**
     * Performs a binary search to find the largest text size that will still fit within the size
     * available to this view.
     */
    @Composable
    @OptIn(ExperimentalTextApi::class)
    private fun findLargestTextSizeWhichFits(
        text: AnnotatedString,
        textStyle: TextStyle,
        constraints: Constraints
    ): Int {
        val sizesCount = mAutoSizeTextSizesInPx.size
        check(sizesCount != 0) { "No available text sizes to choose from." }
        var bestSizeIndex = 0
        var lowIndex = 1 // bestSizeIndex + 1
        var highIndex = sizesCount - 1
        var sizeToTryIndex: Int
        val textMeasurer = rememberTextMeasurer()
        while (lowIndex <= highIndex) {
            sizeToTryIndex = (lowIndex + highIndex) / 2
            if (suggestedSizeFitsInSpace(
                    textMeasurer,
                    text = text,
                    style = textStyle,
                    suggestedSizeInPx = mAutoSizeTextSizesInPx[sizeToTryIndex],
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

    @Composable
    @OptIn(ExperimentalTextApi::class)
    private fun suggestedSizeFitsInSpace(
        textMeasurer: TextMeasurer,
        text: AnnotatedString,
        style: TextStyle,
        constraints: Constraints,
        suggestedSizeInPx: Int
    ): Boolean {
        val suggestedFontSize = with(LocalDensity.current) { suggestedSizeInPx.toSp() }
        val textLayoutResult = textMeasurer.measure(
            text = text,
            style = style.copy(fontSize = suggestedFontSize),
            overflow = TextOverflow.Ellipsis,
            softWrap = true,
            constraints = constraints
        )
        return !textLayoutResult.hasVisualOverflow
    }

    /**
     * @return `true` if this widget supports auto-sizing text and has been configured to
     * auto-size.
     */
    private fun isAutoSizeEnabled(): Boolean {
        return supportsAutoSizeText() && mAutoSizeTextType != AUTO_SIZE_TEXT_TYPE_NONE
    }

    private fun supportsAutoSizeText() = true

    companion object {
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