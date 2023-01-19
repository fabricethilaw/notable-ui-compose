package com.notableui.avatar

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isFinite
import androidx.compose.ui.unit.isUnspecified
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import java.io.File
import java.nio.ByteBuffer
import okhttp3.HttpUrl

/**
 *
 * Avatars are typically used to represent circular user profile pictures.
 * This component will allow you to dynamically size, add an outline and a status badge to images and icons.
 *
 * @param modifier Modifier to be applied to the layout corresponding to the avatar.
 * @param source The default supported data types are: [String], [Uri], [HttpUrl], [File], [DrawableRes], [Drawable], [Bitmap], [ByteArray], [ByteBuffer]
 * @param contentDescription Text used by accessibility services to describe what this avatar represents.
 * @param shape The avatar image will be clipped to this [RoundedCornerShape] shape. The default shape is [CircleShape] (i.e RoundedCornerShape(percent = 50)) .
 * @param imageSize Represents the target size of the image. The default value is 56 dp.
 * @param placeholder A [Painter] that is displayed while the image is loading.
 * @param error A [Painter] that is displayed when the image request is unsuccessful.
 * @param fallback A [Painter] that is displayed when the request's [ImageRequest.data] is null.
 * @param imageScale Optional scale parameter used to determine the aspect ratio scaling to be used if the bounds are a different size from the intrinsic size of the [AsyncImagePainter].
 * @param outline Optional slightly distanced outline border around the Avatar image.
 * @param outlineOffset The distance between the outline border and the Avatar image. Default value is 0 dp.
 * @param badge Optional badge indicating whether the represented user profile is online.
 */
@Composable
public fun Avatar(
    modifier: Modifier = Modifier,
    source: Any,
    contentDescription: String?,
    shape: RoundedCornerShape = CircleShape,
    imageSize: Dp = 56.dp,
    placeholder: Painter? = null,
    error: Painter? = null,
    fallback: Painter? = null,
    imageScale: ContentScale = ContentScale.Fit,
    outline: BorderStroke? = null,
    outlineOffset: Dp = 0.dp,
    badge: AvatarBadge? = null,
) {
    BoxWithConstraints(modifier) {
        val constraints = decoupledConstraints(
            imageSize = adjustImageSizeWithOutline(outline, imageSize),
            outlineThickness = outline?.width ?: 0.dp,
            outlineOffset = outlineOffset,
            badgeSize = badge?.size ?: 0.dp
        )

        ConstraintLayout(
            constraints, modifier = Modifier.wrapContentSize()
        ) {
            // Drawing avatar outline
            when {
                outline != null -> {
                    OutlineDrawer(shape, outline)
                    // First, draw the outer stroke of the online status badge
                    when {
                        badge != null -> {
                            OuterStrokeOfOnlineBadge(badge)
                        }
                    }
                    // Then draw the image
                    ImageDrawer(
                        shape,
                        outlineOffset,
                        imageSize,
                        source,
                        contentDescription,
                        placeholder,
                        error,
                        fallback,
                        imageScale
                    )
                }
                else -> {
                    // First,draw the image
                    ImageDrawer(
                        shape,
                        outlineOffset,
                        imageSize,
                        source,
                        contentDescription,
                        placeholder,
                        error,
                        fallback,
                        imageScale
                    )
                    // Then draw the outer stroke of the online status badge
                    // This order will makes the badge to overlap the image
                    when {
                        badge != null -> {
                            OuterStrokeOfOnlineBadge(badge)
                        }
                    }
                }
            }

            // Drawing the inner stroke of the online status badge
            when {
                badge != null -> {
                    InnerStrokeOfOnlineBadge(badge)
                }
            }

            // Drawing the fill of the online status badge
            when {
                badge != null -> {
                    FillOfOnlineBadge(badge)
                }
            }
        }
    }
}

/**
 * If there is an outline to draw around the Avatar, make sure the image size is specified and finite.
 *
 * @param outline  Outline of to draw around the Avatar.
 * @param imageSize The target size to adjust.
 */
private fun adjustImageSizeWithOutline(
    outline: BorderStroke?, imageSize: Dp
): Dp {
    return if (outline == null) imageSize
    else imageSize.coerceToDefaultIfUnspecified()
}

private fun Dp.coerceToDefaultIfUnspecified(): Dp = if (isFinite && !isUnspecified) this else 56.dp

@Composable
private fun ImageDrawer(
    shape: Shape,
    outlineOffset: Dp,
    size: Dp,
    source: Any,
    contentDescription: String?,
    placeholderPainter: Painter? = null,
    errorPainter: Painter? = null,
    fallbackPainter: Painter? = null,
    contentScale: ContentScale
) {
    Box(
        Modifier
            .layoutId(layoutIdImage)
            .clip(shape)
            .background(color = Unspecified)
            .padding(outlineOffset)
    ) {
        AsyncImage(
            modifier = Modifier
                .requiredSize(size)
                .clip(shape),
            placeholder = placeholderPainter,
            error = errorPainter,
            fallback = fallbackPainter,
            model = ImageRequest.Builder(LocalContext.current).data(source).crossfade(true).build(),
            contentDescription = contentDescription,
            contentScale = contentScale
        )
    }
}

@Composable
private fun OutlineDrawer(avatarShape: Shape, outline: BorderStroke?) {
    Surface(Modifier.layoutId(
        layoutOutline
    ), shape = avatarShape, color = Unspecified, border = outline, content = {})
}

@Composable
private fun OuterStrokeOfOnlineBadge(statusBadge: AvatarBadge) {
    val strokeWidth = statusBadge.size.div(7)

    val fillColor =
        if (!statusBadge.isOnline && statusBadge.style == AvatarBadge.Style.NO_OFFLINE_INDICATOR) {
            Unspecified
        } else {
            White
        }

    val borderColor = when (statusBadge.style) {
        AvatarBadge.Style.NO_OFFLINE_INDICATOR -> Unspecified
        AvatarBadge.Style.OFFLINE_INDICATOR_INSIDE -> White
        AvatarBadge.Style.OFFLINE_INDICATOR_ON_EDGE -> {
            if (statusBadge.isOnline) {
                White
            } else {
                statusBadge.offlineColor
            }
        }
    }

    Surface(modifier = Modifier.layoutId(layoutIdBadgeOuterStroke),
        shape = CircleShape,
        color = fillColor,
        border = BorderStroke(strokeWidth, borderColor),
        content = {})
}

@Composable
private fun InnerStrokeOfOnlineBadge(statusBadge: AvatarBadge) {
    val strokeWidth = statusBadge.size.div(7)

    val fillColor = if (statusBadge.style == AvatarBadge.Style.NO_OFFLINE_INDICATOR) {
        Unspecified
    } else {
        White
    }

    val borderColor = when (statusBadge.style) {
        AvatarBadge.Style.OFFLINE_INDICATOR_ON_EDGE -> Unspecified
        AvatarBadge.Style.NO_OFFLINE_INDICATOR -> Unspecified
        AvatarBadge.Style.OFFLINE_INDICATOR_INSIDE -> {
            if (statusBadge.isOnline) {
                Unspecified
            } else {
                statusBadge.offlineColor
            }
        }
    }

    Surface(modifier = Modifier.layoutId(layoutIdBadgeInnerStroke),
        shape = CircleShape,
        color = fillColor,
        border = BorderStroke(strokeWidth, borderColor),
        content = {
          ProgressIndicatorDefaults
        })
}

@Composable
private fun FillOfOnlineBadge(
    statusBadge: AvatarBadge
) {
    Surface(modifier = Modifier.layoutId(layoutIdBadgeFill),
        shape = CircleShape,
        color = if (statusBadge.isOnline) {
            statusBadge.onlineColor
        } else {
            Unspecified
        },
        content = {})
}

private const val layoutIdImage = "image"
private const val layoutOutline = "outline"
private const val layoutIdBadgeOuterStroke = "badgeOuterStroke"
private const val layoutIdBadgeInnerStroke = "badgeInnerStroke"
private const val layoutIdBadgeFill = "badgeFill"

private fun decoupledConstraints(
    imageSize: Dp, outlineThickness: Dp, outlineOffset: Dp, badgeSize: Dp
): ConstraintSet {
    return ConstraintSet {
        val imageRef = createRefFor(layoutIdImage)
        val outlineRef = createRefFor(layoutOutline)
        val badgeOuterStrokeRef = createRefFor(layoutIdBadgeOuterStroke)
        val badgeInnerStrokeRef = createRefFor(layoutIdBadgeInnerStroke)
        val badgeFillRef = createRefFor(layoutIdBadgeFill)

        val badgeInnerElementsSize = badgeSize.times(5).div(7)

        constrain(imageRef) {
            // add the margin to the image constraint
            // in order to preserve the gap between the drawn outline and the image
            top.linkTo(parent.top, margin = outlineThickness)
            start.linkTo(parent.start, margin = outlineThickness)
            bottom.linkTo(parent.bottom, margin = outlineThickness)
            end.linkTo(parent.end, margin = outlineThickness)
            width = Dimension.wrapContent
            height = Dimension.wrapContent
        }

        constrain(outlineRef) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }

        constrain(badgeOuterStrokeRef) {
            val circularTarget =
                if (outlineThickness in arrayOf(0.dp, Unspecified)) imageRef else outlineRef
            val circularRadius = if (outlineThickness in arrayOf(0.dp, Unspecified)) {
                imageSize.div(2)
            } else {
                imageSize.div(2).plus(outlineOffset).plus(outlineThickness)
            }
            // This will position the badge at a relative angle and distance
            // from either the Avatar image or the Avatar  outline.
            circular(
                circularTarget, angle = 135f, distance = circularRadius
            )
            width = Dimension.value(badgeSize)
            height = Dimension.value(badgeSize)
        }

        constrain(badgeInnerStrokeRef) {
            top.linkTo(badgeOuterStrokeRef.top)
            start.linkTo(badgeOuterStrokeRef.start)
            bottom.linkTo(badgeOuterStrokeRef.bottom)
            end.linkTo(badgeOuterStrokeRef.end)
            width = Dimension.value(badgeInnerElementsSize)
            height = Dimension.value(badgeInnerElementsSize)
        }

        constrain(badgeFillRef) {
            top.linkTo(badgeOuterStrokeRef.top)
            start.linkTo(badgeOuterStrokeRef.start)
            bottom.linkTo(badgeOuterStrokeRef.bottom)
            end.linkTo(badgeOuterStrokeRef.end)
            width = Dimension.value(badgeInnerElementsSize.minus(1.dp))
            height = Dimension.value(badgeInnerElementsSize.minus(1.dp))
        }

    }
}