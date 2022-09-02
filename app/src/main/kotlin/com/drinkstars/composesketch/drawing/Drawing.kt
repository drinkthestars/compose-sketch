package com.drinkstars.composesketch.drawing

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.drinkstars.composesketch.HomeScreens
import com.drinkstars.composesketch.Screen
import com.drinkstars.composesketch.Sketches
import com.drinkstars.composesketch.capture.captureAndShare

@Composable
fun CanvasDraw() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        var size by remember { mutableStateOf(IntSize.Zero) }
        var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
        val context = LocalContext.current

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            IconButton(onClick = {
                captureAndShare(
                    width = size.width,
                    height = size.height,
                    rect = boundsInWindow.toAndroidRect(),
                    context = context
                )
            }) {
                Icon(imageVector = Icons.Outlined.Screenshot, contentDescription = null)
            }
        }
        SimpleCircleWithCanvas(modifier = Modifier
            .fillMaxSize(0.7f)
            .onGloballyPositioned {
                size = it.size
                boundsInWindow = it.boundsInWindow()
            })
    }
}

@Composable
fun BoxDraw() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        var size by remember { mutableStateOf(IntSize.Zero) }
        var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
        val context = LocalContext.current

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        ) {
            IconButton(onClick = {
                captureAndShare(
                    width = size.width,
                    height = size.height,
                    rect = boundsInWindow.toAndroidRect(),
                    context = context
                )
            }) {
                Icon(imageVector = Icons.Outlined.Screenshot, contentDescription = null)
            }
        }
        SimpleCircleWithBox(modifier = Modifier
            .fillMaxSize(0.7f)
            .onGloballyPositioned {
                size = it.size
                boundsInWindow = it.boundsInWindow()
            })
    }
}

@Composable
private fun SimpleCircleWithCanvas(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .padding(12.dp)
            .border(1.dp, Color.DarkGray),
        onDraw = { // this: Dra
            val points = listOf(
                Offset(0f, 0f),
                Offset(this.size.width, 0f),
                Offset(0f, this.size.height),
                Offset(this.size.width, this.size.height),
                Offset(this.size.width / 2f, this.size.height / 2f)
            )

            // Draws circle at this.center
            drawCircle(
                color = Color.DarkGray,
                radius = 200f
            )
            // Draws points at their Offsets
            drawPoints(
                points = points,
                pointMode = PointMode.Points,
                color = Color.Magenta,
                cap = StrokeCap.Round,
                strokeWidth = 30f
            )
        }
    )
}

@Composable
private fun SimpleCircleWithBox(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(12.dp)
            .border(1.dp, Color.DarkGray)
            .drawBehind { // this: Dra
                val points = listOf(
                    Offset(0f, 0f),
                    Offset(this.size.width, 0f),
                    Offset(0f, this.size.height),
                    Offset(this.size.width, this.size.height),
                    Offset(this.size.width / 2f, this.size.height / 2f)
                )

                // Draws circle at this.center
                drawCircle(
                    color = Color.DarkGray,
                    radius = 200f
                )
                // Draws points at their Offsets
                drawPoints(
                    points = points,
                    pointMode = PointMode.Points,
                    color = Color.Magenta,
                    cap = StrokeCap.Round,
                    strokeWidth = 30f
                )
            },

        )
}

private val DrawingScreens = listOf(
    Screen("CanvasDraw") { CanvasDraw() },
    Screen("BoxDraw") { BoxDraw() }
)

@Composable
fun Drawing() {
    Sketches(
        home = HomeScreens.Drawing,
        screens = DrawingScreens
    )
}
