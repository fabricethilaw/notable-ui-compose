package com.notable.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest

@Composable
public fun CircleAvatar(
  modifier: Modifier = Modifier,
  src: Any,
  size: Dp,
  contentDescription: String? = null,
  backgroundColor: Color,
  onError: ((Throwable) -> Unit)? = null,
  contentScale: ContentScale = ContentScale.Crop,
  altContent: @Composable (BoxScope.() -> Unit)?,
) {
  var imageFailed by remember {
    mutableStateOf(false)
  }

  // Image container.
  Box(
    modifier = modifier
      .requiredSize(size)
      .clip(CircleShape)
      .background(backgroundColor, CircleShape),
    contentAlignment = Alignment.Center,
  ) {
    AsyncImage(
      modifier = Modifier
        .requiredSize(size)
        .clip(CircleShape)
        .background(backgroundColor),
      model = ImageRequest.Builder(LocalContext.current).data(src).crossfade(true).build(),
      contentDescription = contentDescription,
      contentScale = contentScale, onError = { error: AsyncImagePainter.State.Error ->
        imageFailed = true
        if (onError != null) {
          onError(error.result.throwable)
        }
      }
    )

    // Fallback to alternative content
    if (imageFailed && altContent != null) {
      AlternativeContent(
        containerSurfaceColor = backgroundColor
      ) {
        this.altContent()
      }
    }
  }
}

@Composable
private fun AlternativeContent(
  containerSurfaceColor: Color,
  content: @Composable () -> Unit
) {
  Box(
    contentAlignment = Alignment.Center
  ) {
    CompositionLocalProvider(
      LocalContentColor provides contentColorFor(containerSurfaceColor)
    ) {
      val style = MaterialTheme.typography.button.copy(fontSize = 11.sp)
      ProvideTextStyle(
        value = style,
        content = { content() }
      )
    }
  }
}

