package com.drinkstars.composesketch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.drinkstars.composesketch.sketch.Sketch
import kotlin.math.sin

private val Padding = 50.dp
private const val DotCount = 15

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .background(Color.DarkGray), contentAlignment = Alignment.Center
    ) {
        Sketch(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .padding(vertical = Padding, horizontal = 10.dp),
            onDraw = { time ->
                val (width, height) = this.size
                (0 until DotCount).forEach { x ->
                    (0 until DotCount).forEach { y ->
                        // working between 0 and 1 aka U/V Space
                        val u = x / (DotCount - 1).toFloat()
                        val v = y / (DotCount - 1).toFloat()

                        // lerp to get a value between 0 and 1
                        val posX = lerp(
                            min = 0f, max = width, norm = u
                        )
                        val posY = lerp(
                            min = 0f, max = height, norm = v
                        )

                        val offset = Offset(posX, posY)
                        drawCircle(
                            color = Color.White,
                            center = offset.copy(
                                y = offset.y + map(
                                    sin(offset.x * 4f + time * 7f),
                                    -1f, 1f,
                                    -10f, 50f
                                )
                            ),
                            radius = map(
                                sin(x * 10f + time * 10f),
                                -1f, 1f,
                                2f, 20f
                            ),
//                            style = Stroke(3f)
                        )
                    }
                }
            }
        )
        Box(
            modifier
                .fillMaxSize()
                .background(Color(0x971A2346)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { },
                    colors = buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.DarkGray
                    )
                ) {
                    Text("LOGIN")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { },
                    colors = buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.DarkGray
                    )
                ) {
                    Text("SIGN UP")
                }
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}
