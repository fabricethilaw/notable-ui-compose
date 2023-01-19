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

package com.example.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.widget.TextViewCompat
import com.example.sample.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DefaultPreview()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
    Text
}

// Alignment Lines allows to align something other than top, bottom
// or center of a layout. textBaseline allows to align the content of the layout
// to the baseline of its child text.
@Composable
fun AlignedIconToTextBaseline() {
    Row {
        Icon(
            contentDescription = "",
            modifier = Modifier
                .size(10.dp)
                .alignBy { it.measuredHeight },
            painter = painterResource(R.drawable.ic_launcher_background),
        )
        Text(
            modifier = Modifier
                .padding(start = 8.dp)
                .alignByBaseline(),
            text = "3min"
        )
    }

}

/**
 * Alignment line in Containers.
 *
 * Alignment passes trough parent layouts, allowing for example to make baseline alignment to the nested child of a button.
 */
@Composable
fun AlignmentToButtonBaseline() {
    Row {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = "Johnny"
        )
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier.alignByBaseline()
        ) {
            Text("FOLLOW")
        }
    }
}

/**
 * Consider wanting to create a fixed-size blue box and centered within its parent.
 */
@Composable
fun BoxWithCenteredContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.Center)
            .size(100.dp) // Or whatever size.
            .background(Color.Blue) // Or whatever background.
    ) {
        // Content
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        Column {
            AlignedIconToTextBaseline()
            AlignmentToButtonBaseline()
            ColumnWithIntrinsicMinWidth()
        }
    }
}

/**
 * To constraint a column to be just wide enough to display its content, set Intrinsic.Min to its width.
 * In this example, the Text's minimum intrinsic with is the with that has one word up each line. It wraps the column conte
 */
@Preview
@Composable
fun ColumnWithIntrinsicMinWidth() {
    Column(modifier = Modifier.width(IntrinsicSize.Min)) {
        val body = MaterialTheme.typography.body2
        Text(modifier = Modifier.fillMaxWidth(), text = "Refresh", style = body)
        Text(modifier = Modifier.fillMaxWidth(), text = "Settings", style = body)
        Text(modifier = Modifier.fillMaxWidth(), text = "Send Feedback", style = body)
        Text(modifier = Modifier.fillMaxWidth(), text = "Help", style = body)
        Text(modifier = Modifier.fillMaxWidth(), text = "Sign out", style = body)
    }
}

/**
 * To constraint a column to be just wide enough to display its content, set Intrinsic.Min to its width.
 */
@Composable
fun ColumnWithIntrinsicMaxWith() {
    Column(modifier = Modifier.width(IntrinsicSize.Min)) {
        val body = MaterialTheme.typography.body2
        Text("Refresh", style = body)
        Text("Settings", style = body)
        Text("Send Feedback", style = body)
        Text("Help", style = body)
        Text("Sign out", style = body)
    }
}