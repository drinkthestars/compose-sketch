package com.drinkstars.composesketch

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.drinkstars.composesketch.sketch.Sketch
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PolygonsSimple() {
    val path = remember { Path() }
    val nbPoints = remember { 5 }
    val nbForms = remember { 15 }
    val radiusMin = remember { 10f }
    val radiusMax = remember { 700f }

    Sketch(
        modifier = Modifier.fillMaxSize(),
        speed = 1f,
        onDraw = { value ->
            path.reset()
            val (width, height) = size
            for (n in 0 until nbForms) {
                val r = map(
                    value = n.toFloat(),
                    sourceMin = 0f,
                    sourceMax = (nbForms.toFloat() - 1f),
                    destMin = radiusMin,
                    destMax = radiusMax
                )
                circle(
                    color = Color.hsv(
                        hue = map(
                            n.toFloat(),
                            0f,
                            (nbForms.toFloat() - 1f),
                            50f,
                            350f
                        ), saturation = 1f, value = 1f
                    ),
                    nbPoints = nbPoints,
                    r = r,
                    center = this.center,
                    shapeRotation = map(sin(value), -1f, 1f, 0f, TWO_PI.toFloat()),
                    path = path,
                    radiusMax = radiusMax,
                    radiusMin = radiusMin
                )
            }
        }
    )
}

@Composable
fun PolygonsComplex() {
    val path = remember { Path() }
    val nbPoints = remember { 5 }
    val nbForms = remember { 15 }
    val radiusMin = remember { 10f }
    val radiusMax = remember { 700f }

    Sketch(
        modifier = Modifier.fillMaxSize(),
        speed = 1f,
        onDraw = { value ->
            path.reset()
            val (width, height) = size
            for (n in 0 until nbForms) {
                val r = map(
                    value = n.toFloat(),
                    sourceMin = 0f,
                    sourceMax = (nbForms.toFloat() - 1f),
                    destMin = radiusMin,
                    destMax = radiusMax
                )
                circle(
                    color = Color.hsv(
                        hue = map(
                            n.toFloat(),
                            0f,
                            (nbForms.toFloat() - 1f),
                            50f,
                            350f
                        ), saturation = 1f, value = 1f
                    ),
                    nbPoints = nbPoints,
                    r = r,
                    center = this.center,
                    shapeRotation = map(sin(value), -1f, 1f, 0f, TWO_PI.toFloat()),
                    path = path,
                    radiusMax = radiusMax,
                    radiusMin = radiusMin
                )
            }
        }
    )
}

@Composable
fun PolygonsColor() {
    val path = remember { Path() }
    val nbPoints = remember { 5 }
    val nbForms = remember { 1 }
    val radiusMin = remember { 10f }
    val radiusMax = remember { 700f }

    Sketch(
        modifier = Modifier.fillMaxSize(),
        speed = 1f,
        onDraw = { time ->
            path.reset()
            val (width, height) = size
            for (n in 0 until nbForms) {
                val r = map(
                    value = n.toFloat(),
                    sourceMin = 0f,
                    sourceMax = (nbForms.toFloat() - 1f),
                    destMin = radiusMin,
                    destMax = radiusMax
                )
                simpleCircle(
                    color = Color.hsv(
                        hue = map(
                            n.toFloat(),
                            0f,
                            (nbForms.toFloat() - 1f),
                            50f,
                            350f
                        ), saturation = 1f, value = 1f
                    ),
                    nbPoints = nbPoints,
                    r = r,
                    center = this.center,
                    shapeRotation = map(sin(time), -1f, 1f, 0f, TWO_PI.toFloat()),
                    path = path
                )
            }
        }
    )
}

fun DrawScope.simpleCircle(
    center: Offset,
    shapeRotation: Float = 0f,
    nbPoints: Int,
    r: Float,
    path: Path,
    color: Color
) {
    for (i in 0 until nbPoints) {
        val rad = shapeRotation + i.toFloat() * TWO_PI / nbPoints.toFloat() - HALF_PI.toFloat()
        val x = r * cos(rad).toFloat() + center.x
        val y = r * sin(rad).toFloat() + center.y
        when (i) {
            0 -> path.moveTo(x, y)
            else -> path.lineTo(x, y)
        }
    }
    path.close()
    val style = Stroke(5f)
    val alpha = 1f
    drawPath(
        path, color = color, style = style, alpha = alpha
    )
}

fun DrawScope.circle(
    center: Offset,
    shapeRotation: Float = 0f,
    nbPoints: Int,
    r: Float,
    radiusMax: Float,
    radiusMin: Float,
    path: Path,
    color: Color
) {
    for (i in 0 until nbPoints) {
        val rad = shapeRotation + i.toFloat() * TWO_PI / nbPoints.toFloat() - HALF_PI.toFloat()
        val x = r * cos(rad).toFloat() + center.x
        val y = r * sin(rad).toFloat() + center.y
        when (i) {
            0 -> path.moveTo(x, y)
            else -> path.lineTo(x, y)
        }
    }
    path.close()
    val style = Stroke(
        width = map(
            value = r,
            sourceMin = radiusMin,
            sourceMax = radiusMax,
            destMin = 15f,
            destMax = 2f
        )
    )
    val alpha = map(
        value = r,
        sourceMin = radiusMin,
        sourceMax = radiusMax,
        destMin = 0.5f,
        destMax = 0.1f
    )
    drawPath(
        path, color = color, style = style, alpha = alpha
    )
}
