package com.notable.ui.demos.responsivetext

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.notable.ui.text.ResponsiveText
import com.notable.ui.text.TextScale

@Composable
fun DemoEnableAutoTextSizing() {
  val longText = "The world is changed by your example, not by your opinion."
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .height(160.dp)
  ) {
    ResponsiveText(
      text = longText, textScale = TextScale.Uniform
    )
  }
}