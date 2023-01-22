package com.notable.ui.demos.responsivetext

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notable.ui.text.ResponsiveText
import com.notable.ui.text.TextScale

@Composable
fun DemoOptimalTextOverflow() {
  val veryLongText =
    """The traveler, a young woman named Emma, had been on the road for weeks. She had left her hometown in search of adventure and a chance to reconnect with nature. As she made her way through the remote wilderness, she couldn't help but feel a sense of peace and contentment.

One morning, as Emma was setting up her tent for the night, she couldn't help but notice the beautiful colors of the sky as the sun began to rise. She quickly packed her things and set off towards the east, eager to catch the sunrise. As she crested a small hill, Emma was greeted by a breathtaking sight. The sky was painted in a spectrum of colors, from deep oranges and pinks to soft purples and blues. The sun slowly rose over the horizon, casting a warm glow over the landscape. Emma couldn't help but feel a deep sense of awe and inspiration as she watched the sunrise.She set up her camera and began capturing the moment, feeling a deep sense of gratitude and joy. The landscape was transformed by the light of the rising sun, and Emma was able to capture the true essence of the wilderness and how it changed the mood and atmosphere.
""".trimMargin()

  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .height(160.dp)
  ) {
    ResponsiveText(
      text = veryLongText,
      textScale = TextScale.SizeRange(
        minTextSize = 14.sp,
        maxTextSize = 26.sp,
        stepGranularity = 1.sp
      ),
      overflow = TextOverflow.Ellipsis
    )
  }
}