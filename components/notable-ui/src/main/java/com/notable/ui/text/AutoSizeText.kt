package com.notable.ui.text

import android.util.Log
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import java.util.*

/**
 * Specifies auto-size configuration for displaying a text with [AutoSizeText].
 */
public sealed interface AutoSizeOption {
  /**
   * Do not scale the text size.
   */
  public object None : AutoSizeOption

  /**
   * Scale the text size both horizontally and vertically to fit within the
   * container by using the default auto-size configuration.
   */
  public object Uniform : AutoSizeOption

  /**
   * Represents a list of sizes in text units from which [AutoSizeText] should pick the optimal value.
   * If at least one value from the sizes is valid
   * then the auto-size behavior is enabled for [AutoSizeText].
   */
  public data class PresetSizes(public val sizes: List<TextUnit>) : AutoSizeOption

  /**
   * Provide a fine-grained configuration for text auto sizing.
   * @param minTextSize the minimum text size available for auto-size. Must be greater than 0 in text units.
   * @param maxTextSize the maximum text size available for auto-size. Must be greater than 0 in text units.
   * @param stepGranularity the auto-size step granularity in text units. It is used in conjunction with
   * the minimum and maximum text size in order to build the set of text sizes the system uses to
   * choose from when auto-sizing.
   *
   * @throws IllegalArgumentException if any of the auto text-sizing configuration params are invalid.
   *
   */
  public data class SizeRange(
    public val minTextSize: TextUnit,
    public val maxTextSize: TextUnit,
    public val stepGranularity: TextUnit
  ) : AutoSizeOption
}


/**
 * High level stateless element that displays auto sizeable text and provides semantics / accessibility information.
 * To display the text, this component firstly performs a binary search to find the largest text size
 * that will still fit within the available space. Then the text is drawn once with the optimal size.
 *
 *
 * The default [style] uses the [LocalTextStyle] provided by the [MaterialTheme] / components. If
 * you are setting your own style, you may want to consider first retrieving [LocalTextStyle],
 * and using [TextStyle.copy] to keep any theme defined attributes, only modifying the specific
 * attributes you want to override.
 *
 * For ease of use, commonly used parameters from [TextStyle] are also present here. The order of
 * precedence is as follows:
 * - If a parameter is explicitly set here (i.e, it is _not_ `null` or [TextUnit.Unspecified]),
 * then this parameter will always be used.
 * - If a parameter is _not_ set, (`null` or [TextUnit.Unspecified]), then the corresponding value
 * from [style] will be used instead.
 *
 * Additionally, for [color], if [color] is not set, and [style] does not have a color, then
 * [LocalContentColor] will be used with an alpha of [LocalContentAlpha]- this allows this
 * [Text] or element containing this [Text] to adapt to different background colors and still
 * maintain contrast and accessibility.
 *
 * @param text The text to be displayed.
 * @param modifier [Modifier] to apply to this layout node.
 * @param color [Color] to apply to the text. If [Color.Unspecified], and [style] has no color set,
 * this will be [LocalContentColor].
 * @param fontSize The size of glyphs to use when painting the text. See [TextStyle.fontSize].
 * @param fontStyle The typeface variant to use when drawing the letters (e.g., italic).
 * See [TextStyle.fontStyle].
 * @param fontWeight The typeface thickness to use when painting the text (e.g., [FontWeight.Bold]).
 * @param fontFamily The font family to be used when rendering the text. See [TextStyle.fontFamily].
 * @param letterSpacing The amount of space to add between each letter.
 * See [TextStyle.letterSpacing].
 * @param textDecoration The decorations to paint on the text (e.g., an underline).
 * See [TextStyle.textDecoration].
 * @param textAlign The alignment of the text within the lines of the paragraph.
 * See [TextStyle.textAlign].
 * @param lineHeight Line height for the [Paragraph] in [TextUnit] unit, e.g. SP or EM.
 * See [TextStyle.lineHeight].
 * @param overflow How visual overflow should be handled.
 * @param softWrap Whether the text should break at soft line breaks. If false, the glyphs in the
 * text will be positioned as if there was unlimited horizontal space. If [softWrap] is false,
 * [overflow] and TextAlign may have unexpected effects.
 * @param maxLines An optional maximum number of lines for the text to span, wrapping if
 * necessary. If the text exceeds the given number of lines, it will be truncated according to
 * [overflow] and [softWrap]. If it is not null, then it must be greater than zero.
 * @param onTextLayout Callback that is executed when a new text layout is calculated. A
 * [TextLayoutResult] object that callback provides contains paragraph information, size of the
 * text, baselines and other details. The callback can be used to add additional decoration or
 * functionality to the text. For example, to draw selection around the text.
 * @param style Style configuration for the text such as color, font, line height etc.
 * @param autoSizeOption  The auto-size configuration. It specifies whether this component should automatically scale the text to try to perfectly fit within the layout bounds.
 *
 * @throws IllegalArgumentException if any of the auto text-sizing option params are invalid.
 */
@Composable
public fun AutoSizeText(
  text: String,
  modifier: Modifier = Modifier,
  color: Color = Color.Unspecified,
  fontSize: TextUnit = TextUnit.Unspecified,
  fontStyle: FontStyle? = null,
  fontWeight: FontWeight? = null,
  fontFamily: FontFamily? = null,
  letterSpacing: TextUnit = TextUnit.Unspecified,
  textDecoration: TextDecoration? = null,
  textAlign: TextAlign? = null,
  lineHeight: TextUnit = TextUnit.Unspecified,
  overflow: TextOverflow = TextOverflow.Clip,
  softWrap: Boolean = true,
  maxLines: Int = Int.MAX_VALUE,
  onTextLayout: (TextLayoutResult) -> Unit = {},
  style: TextStyle = LocalTextStyle.current,
  autoSizeOption: AutoSizeOption
) {
  val mergedStyle = style.merge(
    TextStyle(
      color = color,
      fontSize = fontSize,
      fontWeight = fontWeight,
      textAlign = textAlign,
      lineHeight = lineHeight,
      fontFamily = fontFamily,
      textDecoration = textDecoration,
      fontStyle = fontStyle,
      letterSpacing = letterSpacing
    )
  )
  if (autoSizeOption == AutoSizeOption.None) {
    Text(
      text = text,
      fontSize = fontSize,
      style = mergedStyle,
      overflow = overflow,
      softWrap = softWrap,
      maxLines = maxLines,
      onTextLayout = onTextLayout
    )
    return
  }
  AutoTextBox(
    modifier,
    autoSizeOption,
    buildAnnotatedString { append(text) },
    mergedStyle,
    maxLines,
    overflow,
    softWrap,
    onTextLayout
  )
}


/**
 * High level stateless element that displays auto sizeable text and provides semantics / accessibility information.
 * To display the text, this component firstly performs a binary search to find the largest text size
 * that will still fit within the available space. Then the text is drawn once with the optimal size.
 *
 *
 * The default [style] uses the [LocalTextStyle] provided by the [MaterialTheme] / components. If
 * you are setting your own style, you may want to consider first retrieving [LocalTextStyle],
 * and using [TextStyle.copy] to keep any theme defined attributes, only modifying the specific
 * attributes you want to override.
 *
 * For ease of use, commonly used parameters from [TextStyle] are also present here. The order of
 * precedence is as follows:
 * - If a parameter is explicitly set here (i.e, it is _not_ `null` or [TextUnit.Unspecified]),
 * then this parameter will always be used.
 * - If a parameter is _not_ set, (`null` or [TextUnit.Unspecified]), then the corresponding value
 * from [style] will be used instead.
 *
 * Additionally, for [color], if [color] is not set, and [style] does not have a color, then
 * [LocalContentColor] will be used with an alpha of [LocalContentAlpha]- this allows this
 * [Text] or element containing this [Text] to adapt to different background colors and still
 * maintain contrast and accessibility.
 *
 * @param text The text to be displayed.
 * @param modifier [Modifier] to apply to this layout node.
 * @param color [Color] to apply to the text. If [Color.Unspecified], and [style] has no color set,
 * this will be [LocalContentColor].
 * @param fontSize The size of glyphs to use when painting the text. See [TextStyle.fontSize].
 * @param fontStyle The typeface variant to use when drawing the letters (e.g., italic).
 * See [TextStyle.fontStyle].
 * @param fontWeight The typeface thickness to use when painting the text (e.g., [FontWeight.Bold]).
 * @param fontFamily The font family to be used when rendering the text. See [TextStyle.fontFamily].
 * @param letterSpacing The amount of space to add between each letter.
 * See [TextStyle.letterSpacing].
 * @param textDecoration The decorations to paint on the text (e.g., an underline).
 * See [TextStyle.textDecoration].
 * @param textAlign The alignment of the text within the lines of the paragraph.
 * See [TextStyle.textAlign].
 * @param lineHeight Line height for the [Paragraph] in [TextUnit] unit, e.g. SP or EM.
 * See [TextStyle.lineHeight].
 * @param overflow How visual overflow should be handled.
 * @param softWrap Whether the text should break at soft line breaks. If false, the glyphs in the
 * text will be positioned as if there was unlimited horizontal space. If [softWrap] is false,
 * [overflow] and TextAlign may have unexpected effects.
 * @param maxLines An optional maximum number of lines for the text to span, wrapping if
 * necessary. If the text exceeds the given number of lines, it will be truncated according to
 * [overflow] and [softWrap]. If it is not null, then it must be greater than zero.
 * @param onTextLayout Callback that is executed when a new text layout is calculated. A
 * [TextLayoutResult] object that callback provides contains paragraph information, size of the
 * text, baselines and other details. The callback can be used to add additional decoration or
 * functionality to the text. For example, to draw selection around the text.
 * @param style Style configuration for the text such as color, font, line height etc.
 * @param autoSizeOption  The auto-size configuration. It specifies whether this component should automatically scale the text to try to perfectly fit within the layout bounds.
 *
 * @throws IllegalArgumentException if any of the auto text-sizing option params are invalid.
 */
@Composable
public fun AutoSizeText(
  text: AnnotatedString,
  modifier: Modifier = Modifier,
  color: Color = Color.Unspecified,
  fontSize: TextUnit = TextUnit.Unspecified,
  fontStyle: FontStyle? = null,
  fontWeight: FontWeight? = null,
  fontFamily: FontFamily? = null,
  letterSpacing: TextUnit = TextUnit.Unspecified,
  textDecoration: TextDecoration? = null,
  textAlign: TextAlign? = null,
  lineHeight: TextUnit = TextUnit.Unspecified,
  overflow: TextOverflow = TextOverflow.Clip,
  softWrap: Boolean = true,
  maxLines: Int = Int.MAX_VALUE,
  onTextLayout: (TextLayoutResult) -> Unit = {},
  style: TextStyle = LocalTextStyle.current,
  autoSizeOption: AutoSizeOption
) {
  val mergedStyle = style.merge(
    TextStyle(
      color = color,
      fontSize = fontSize,
      fontWeight = fontWeight,
      textAlign = textAlign,
      lineHeight = lineHeight,
      fontFamily = fontFamily,
      textDecoration = textDecoration,
      fontStyle = fontStyle,
      letterSpacing = letterSpacing
    )
  )
  if (autoSizeOption == AutoSizeOption.None) {
    Text(
      text = text,
      fontSize = fontSize,
      style = mergedStyle,
      overflow = overflow,
      softWrap = softWrap,
      maxLines = maxLines,
      onTextLayout = onTextLayout
    )
    return
  }

  AutoTextBox(
    modifier,
    autoSizeOption,
    text,
    mergedStyle,
    maxLines,
    overflow,
    softWrap,
    onTextLayout
  )
}

@Composable
@OptIn(ExperimentalTextApi::class)
private fun AutoTextBox(
  modifier: Modifier,
  autoSizeOption: AutoSizeOption,
  text: AnnotatedString,
  style: TextStyle,
  maxLines: Int,
  overflow: TextOverflow,
  softWrap: Boolean,
  onTextLayout: (TextLayoutResult) -> Unit
) {
  val autoSizeTextHelper = AutoSizeTextHelper()
  BoxWithConstraints(modifier) {
    val density: Density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()
    val (originalTextSize, optimalTextSize) =
      when (autoSizeOption) {
        is AutoSizeOption.Uniform -> {
          autoSizeTextHelper.setAutoSizeTextTypeWithDefaults(
            density, textMeasurer,
            autoSizeTextType = AutoSizeTextType.Uniform,
            text = buildAnnotatedString { append(text) },
            textStyle = style,
            maxLines = maxLines,
            constraints = constraints
          )
        }
        is AutoSizeOption.SizeRange -> {
          autoSizeTextHelper.setAutoSizeTextTypeUniformWithConfiguration(
            density, textMeasurer,
            autoSizeOption.minTextSize,
            autoSizeOption.maxTextSize,
            autoSizeOption.stepGranularity,
            text = text,
            textStyle = style,
            maxLines = maxLines,
            constraints = constraints
          )
        }
        is AutoSizeOption.PresetSizes -> {
          autoSizeTextHelper.setAutoSizeTextTypeUniformWithPresetSizes(
            density, textMeasurer,
            autoSizeOption.sizes.toTypedArray(),
            text = buildAnnotatedString { append(text) },
            textStyle = style,
            maxLines = maxLines,
            constraints = constraints
          )
        }
        else -> {
          Pair(style.fontSize, style.fontSize)
        }
      }
    Log.i("AutoSizeText", "AutoSizeText: Original: $originalTextSize, optimal: $optimalTextSize")
    Log.i("AutoSizeText", "AutoSizeText: constraints: $constraints")
    Text(
      text = text,
      fontSize = optimalTextSize,
      style = style.copy(fontSize = optimalTextSize),
      overflow = overflow,
      softWrap = softWrap,
      maxLines = maxLines,
      onTextLayout = {
        Log.i("AutoSizeText", "AutoSizeText applied: ${it.layoutInput.style.fontSize}")
        onTextLayout.invoke(it)
      }
    )
  }
}