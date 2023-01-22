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
fun DemoAutoScalingDownText() {
  val longText =
    "As the sun began to set, the traveler stood at the edge of the cliff, taking in the vibrant hues of orange, pink and purple that stretched across the sky. The journey had been long and tiring, but the breathtaking view of the sunset made it all worth it. The traveler felt a sense of peace wash over them, as if all their troubles and worries were being left behind with the setting sun."

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .height(160.dp)
  ) {
    ResponsiveText(
      text = longText,
      textScale = TextScale.Uniform
    )
  }
}
