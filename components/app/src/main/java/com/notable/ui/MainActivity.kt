/*
 * Copyright 2022 Fabrice Thilaw KIKI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.notable.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notable.ui.demos.responsivetext.DemoAutoScalingDownText
import com.notable.ui.demos.responsivetext.DemoDisableAutoTextSizing
import com.notable.ui.demos.responsivetext.DemoEnableAutoTextSizing
import com.notable.ui.demos.responsivetext.DemoOptimalTextOverflow
import com.notable.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      MyApplicationTheme {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = Color(0xFFF1F1F1)
        ) {
          DefaultPreview()
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  MyApplicationTheme {
    Column(
      Modifier
        .padding(16.dp)
        .scrollable(
          rememberScrollState(), orientation = Orientation.Vertical
        )
    ) {
      DemoDisableAutoTextSizing()
      Spacer(modifier = Modifier.height(16.dp))
      DemoEnableAutoTextSizing()
      Spacer(modifier = Modifier.height(16.dp))
      DemoAutoScalingDownText()
      Spacer(modifier = Modifier.height(16.dp))
      DemoOptimalTextOverflow()
    }
  }
}
