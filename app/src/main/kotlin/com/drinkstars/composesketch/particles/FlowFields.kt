package com.drinkstars.composesketch.particles

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.drinkstars.composesketch.sketch.RedrawSketch
import com.drinkstars.composesketch.sketch.fps
import glm_.glm
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

private val Padding = 16.dp
private const val SizeMean = 13f
private const val SizeStdDev = 1f
private const val SatMin = 0.2f
private const val SatMax = 0.8f
private const val DotCount = 300
private const val Scale = 5f
private const val Magnitude = 285f

private class Field(
    val xyOffsets: List<Offset> = emptyList(),
    val particles: List<Particle> = emptyList(),
    val vectors: Array<Offset> = emptyArray(),
    val angles: Array<Float> = emptyArray()
)

@Composable
fun FlowField(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        val fps = fps()
        Text(text = "fps = ${fps.value}", modifier.align(Alignment.TopStart))
        FlowFieldSketch(modifier.align(Alignment.Center))
    }
}

@Composable
private fun FlowFieldSketch(modifier: Modifier = Modifier) {
    val density = LocalDensity.current
    val padding = with(density) { Padding.toPx() }
    val width = remember { mutableStateOf(0f) }
    val height = remember { mutableStateOf(0f) }

    val color = remember { Color.Black.copy(alpha = 0.4f) }
    val flowField = remember { mutableStateOf(Field()) }
    val resolution = remember { mutableStateOf(64.8f) }

    RedrawSketch(
        modifier
            .fillMaxSize(0.9f)
            .aspectRatio(1f)
            .onSizeChanged { size ->
                Snapshot.withMutableSnapshot {
                    width.value = size.width.toFloat()
                    height.value = size.height.toFloat()
                    Log.i("WARP", "size changed: $size")
                    resolution.value = size.width.toFloat() / 15
                    flowField.value = flowField(
                        padding = padding,
                        width = size.width.toFloat(),
                        height = size.height.toFloat()
                    )
                }
            }
            .background(Color.Transparent)
//            .draggable(
//                orientation = Orientation.Vertical,
//                state = rememberDraggableState { delta ->
//                    val originalY = draggedY.value
//                    val newY = (originalY + delta).coerceIn(0f, screenHeightPx)
//                    draggedY.value = newY
//                currentSpeed = currentSpeed(draggedY.value, screenHeightPx)
//                }
//            )
    ) { time ->
        drawIntoCanvas { canvas ->
            flowField.value.apply {
                // Render flow
//                renderFlowField(
//                    flowField = this,
//                    canvas = canvas,
//                    time = time
//                )
                // Render particles
                particles.drawAndFollow(
                    color = color,
                    drawScope = this@RedrawSketch,
                    vectors = vectors,
                    resolution = resolution.value,
                    count = DotCount
                )
            }
        }
    }
}

private fun DrawScope.renderFlowField(
    flowField: Field,
    time: Float,
    canvas: Canvas
) {
//    val angle = 25f
//    canvas.save()
//    canvas.rotate(-2f * angle, 10f, 10f)
//    drawLine(
//        start = Offset(10f, 10f),
//        end = Offset(10f + 40, 10f + 40),
//        color = Color.Yellow
//    )
//    canvas.restore()
//
//    val radians = (angle) * PI.toFloat() / 180f
//    val vectorX = Scale * cos(radians)
//    val vectorY = Scale * sin(radians)
//    val scaledXY = Offset(vectorX, vectorY)
//    drawPoints(
//        points = listOf(Offset(10f + 40f, 10f - 40f)),
//        pointMode = PointMode.Points,
//        color = Color.Yellow,
//        strokeWidth = 10f,
//        cap = StrokeCap.Round
//    )

    flowField.xyOffsets.forEachIndexed { i, xyOffset ->

        // ANIMATED FLOW FIELD

        // PREDEFINED ANGLE
        val x = i % DotCount
        val y = i / DotCount
        val index = x + y * DotCount

        val rotRad = flowField.angles[index]

        // NOSIY ANGLE
//        val noiseAngle = Java.glm.perlin(
//            Vec4(
//                x,
//                y,
//                Scale * cos(TWO_PI * time/5),
//                Scale * sin(TWO_PI * time/5)
//            )
//        ) * 40f * glm.PIf / 180f
        val vectorX = (Scale * sin(rotRad)) + xyOffset.x
        val vectorY = (Scale * cos(rotRad)) + xyOffset.y
        drawLine(
            start = xyOffset,
            strokeWidth = 3f,
            end = Offset(vectorX, vectorY),
            color = Color.Black
        )
    }
}

private fun flowField(
    padding: Float,
    width: Float,
    height: Float
): Field {
    val uvOffsets = mutableListOf<Offset>()
    val xyOffsets = mutableListOf<Offset>()
    val vectors = Array(DotCount * DotCount) { Offset.Zero }
    val angles = Array(DotCount * DotCount) { 0f }

    (0 until DotCount).forEach { x ->
        (0 until DotCount).forEach { y ->
            val index = x + y * DotCount

            // working between 0 and 1 aka U/V Space
            val u = x / (DotCount - 1).toFloat()
            val v = y / (DotCount - 1).toFloat()
            uvOffsets.add(Offset(u, v))

            val posx = lerp(padding, width - padding, u)
            val posy = lerp(padding, height - padding, v)
            val perlinNoise = noise(posx, posy)
            val rotationDeg = perlinNoise * Magnitude
            val radians = rotationDeg * PI.toFloat() / 180f

            val vectorX = (1f * sin(radians))
            val vectorY = (1f * cos(radians))

            angles[index] = radians
            vectors[index] = Offset(vectorX, vectorY)

            xyOffsets.add(Offset(posx, posy))
        }
    }

    val particles = xyOffsets.map {
        Particle(
            initPos = it,
            initVel = Offset.Zero,
            width = width,
            height = height
        )
    }

    return Field(
        xyOffsets = xyOffsets,
        particles = particles,
        vectors = vectors,
        angles = angles
    )
}

private fun noise(x: Float, y: Float) = abs(
    glm.perlin(
        Vec2(
            x = x,
            y = y
        )
    )
)

private fun noise(x: Float, y: Float, z: Float) = abs(
    glm.perlin(
        Vec3(
            x = x,
            y = y,
            z = z
        )
    )
)

@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun PreviewSketch2() {
    FlowField()
}
