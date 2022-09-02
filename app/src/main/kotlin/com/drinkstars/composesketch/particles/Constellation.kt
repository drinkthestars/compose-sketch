package com.drinkstars.composesketch.particles

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import com.drinkstars.composesketch.lerp
import com.drinkstars.composesketch.sketch.Sketch
import kotlin.math.absoluteValue

@Preview(showBackground = true)
@Composable
fun Constellation() {
    val particles = remember { mutableStateOf(listOf<Particle>()) }
    val distance = remember { 200f }
    val count = remember { 200 }
    val linePaint = remember { Paint() }
    Sketch(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                particles.value = (0 until count).map {
                    Particle(
                        width = size.width.toFloat(),
                        height = size.height.toFloat(),
                        radius = 15f
                    )
                }
            },
        speed = 0.02f,
        showControls = true,
        onDraw = { _ ->
            val (width, height) = size
            drawIntoCanvas { canvas ->
                particles.value.apply {
                    for (i in 0 until count) {
                        for (j in 0 until count) {
                            val iStar = this[i]
                            val jStar = this[j]

                            val xAbsDist = (iStar.x - jStar.x).absoluteValue
                            val yAbsDist = (iStar.y - jStar.y).absoluteValue

                            if (xAbsDist < distance && yAbsDist < distance) {
//                                if (
//                                    (iStar.x - AttributeKey.position.x).abs() < config.radius &&
//                                    (iStar.y - config.position.y).abs() < config.radius
//                                ) {
//                                val diagonal = glm.distance(Vec2(iStar.x, iStar.y), Vec2(jStar.x, jStar.y))
//                                val diagonal = sqrt((xAbsDist * xAbsDist) + (yAbsDist * yAbsDist))
                                val fraction =
                                    lerp((1 - (xAbsDist + yAbsDist) / (distance * 2)), 0f, 1f)
                                val hue =
                                    com.drinkstars.composesketch.map(jStar.y, 0f, height, 0f, 360f)
                                canvas.drawLine(
                                    p1 = Offset(iStar.x, iStar.y),
                                    p2 = Offset(jStar.x, jStar.y),
                                    paint = linePaint.apply {
                                        this.alpha = fraction
                                        this.strokeWidth = lerp(fraction, 0f, 2f)
                                        this.color = Color.hsv(hue, 1f, 1f)
                                    }
                                )
//                                }
                            }
                        }
                    }
                    draw(drawScope = this@Sketch)
                }
            }
        }
    )
}
