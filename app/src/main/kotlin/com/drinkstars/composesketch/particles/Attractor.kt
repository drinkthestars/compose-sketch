package com.drinkstars.composesketch.particles

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.drinkstars.composesketch.R
import com.drinkstars.composesketch.sketch.Sketch
import glm_.glm.linearRand

private const val TilesX = 90f

private const val ParticleCount = 17
private const val G = 500f
private const val MaxVel = 15f

private class SimpleParticle(
    val width: Float,
    val height: Float,
    val initPos: Offset,
    val color: Color
) {
    var pos = initPos
    var vel = Offset(0f, 0f)

    fun show(drawScope: DrawScope) {
        drawScope.apply {
            drawCircle(center = pos, radius = 10f, color = color)
        }
    }

    fun update() {
        pos += vel
//        val newVelX = (vel.x + acc.x).coerceIn(-MaxVel, MaxVel)
//        val newVelY = (vel.y + acc.y).coerceIn(-MaxVel, MaxVel)
//        vel = Offset(newVelX, newVelY)
    }

    fun flyTo(target: Offset) {
        val force = target - pos
        val dist = force.getDistance().coerceIn(5f, 15f)
        val dir = force / dist
        vel = dir * dist * linearRand(0.05f, 0.1f)

//        val magnitude = G / dist
//        val currentMag = force.getDistance()
//        val newX = force.x * magnitude / currentMag
//        val newY = force.y * magnitude / currentMag
//        acc = Offset(newX, newY)
    }

    fun flyAwayFrom(target: Offset) {
        val force = target - pos
        val dist = force.getDistance()
        if (dist < 20f) {
            val dir = force / dist
            vel = dir * dist * linearRand(0.1f, 0.5f) * -1f
//            acc = dir * dist * linearRand(0.003f, 0.008f) * -1f
        }
    }

    fun returnToInitPos() {
//        acc = Offset(0f, 0f)
        flyTo(initPos)
        if (pos.getDistanceSquared() < 26f) {
            pos = initPos
            vel = Offset(0f, 0f)
//            acc = Offset(0f, 0f)
        }
    }

    fun edges() {
        if (pos.x > width) {
            pos = pos.copy(x = 0f)
        }
        if (pos.x < 0f) {
            pos = pos.copy(x = width)
        }
        if (pos.y > height) {
            pos = pos.copy(y = 0f)
        }
        if (pos.y < 0f) {
            pos = pos.copy(y = height)
        }
    }
}

@Composable
fun Attractor(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AttractorSketch()
    }
}

@Composable
fun AttractorSketch(modifier: Modifier = Modifier) {
    var attractor by remember { mutableStateOf<Offset?>(Offset(300f, 300f)) }
    var size by remember { mutableStateOf(Size.Zero) }
    var particles by remember { mutableStateOf(listOf<SimpleParticle>()) }

    Sketch(
        modifier = modifier
            .fillMaxSize(0.9f)
            .aspectRatio(1f)
            .border(1.dp, Color.DarkGray)
            .padding(12.dp)
            .onSizeChanged {
                val widthf = it.width.toFloat()
                val heightf = it.height.toFloat()
                size = Size(widthf, heightf)
                attractor = Offset(x = it.width / 2f, y = it.height / 2f)
                particles = List(ParticleCount) {
                    val offsetx = linearRand(10f, widthf - 10f)
                    val offsety = linearRand(10f, heightf - 10f)
                    SimpleParticle(
                        width = widthf,
                        height = widthf,
                        initPos = Offset(offsetx, offsety),
                        color = Color(
                            linearRand(0.2f, 0.8f),
                            linearRand(0.2f, 0.8f),
                            linearRand(0.2f, 0.8f)
                        )
                    )
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        attractor = it
                    },
                    onDrag = { change, dragAmount ->
                        val original = attractor ?: change.position
                        val summed = original + dragAmount
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, size.width),
                            y = summed.y.coerceIn(0f, size.height)
                        )
                        attractor = newValue
                    }, onDragEnd = {
                        attractor = null
                    }
                )
            }
    ) {
        attractor?.run {
            drawCircle(
                center = Offset(x, y),
                radius = 25f,
                color = Color.Red
            )
        }
        particles.forEach {
            if (attractor != null) {
                it.flyTo(attractor!!)
            } else {
                it.returnToInitPos()
            }
            it.update()
//            it.edges()
            it.show(this)
        }
    }
}


@Composable
fun RasterizeAttractor() {
    val imageBitmap = createImageBitmap()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(imageBitmap = imageBitmap)
    }
}

@Composable
private fun Image(imageBitmap: ImageBitmap, modifier: Modifier = Modifier) {
    var attractor by remember { mutableStateOf<Offset?>(null) }
    var particles by remember { mutableStateOf(listOf<SimpleParticle>()) }

    val pixelMap = imageBitmap.toPixelMap(
        width = imageBitmap.width, height = imageBitmap.height
    )
    val canvasSize = with(LocalDensity.current) {
        DpSize(imageBitmap.width.toDp(), imageBitmap.height.toDp())
    }
    val tileSize: Float = imageBitmap.width.toFloat() / TilesX

    Sketch(
        showControls = true,
        modifier = modifier
            .size(canvasSize)
            .onSizeChanged {
                for (x in 0 until imageBitmap.width step tileSize.toInt()) {
                    for (y in 0 until imageBitmap.height step tileSize.toInt()) {
                        val pixel = pixelMap[x, y]
                        val offset = Offset(
                            x.toFloat(),
                            y.toFloat()
                        )
                        particles = particles + SimpleParticle(
                            width = imageBitmap.width.toFloat(),
                            height = imageBitmap.height.toFloat(),
                            initPos = offset,
                            color = pixel
                        )
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        attractor = it
                    },
                    onDrag = { change, dragAmount ->
                        val original = attractor ?: change.position
                        val summed = original + dragAmount
                        val newValue = Offset(
                            x = summed.x.coerceIn(0f, imageBitmap.width.toFloat()),
                            y = summed.y.coerceIn(0f, imageBitmap.height.toFloat())
                        )
                        attractor = newValue
                    }, onDragEnd = {
                        attractor = null
                    }
                )
            }
    ) {
        particles.forEach {
            if (attractor != null) {
                it.flyAwayFrom(attractor!!)
            } else {
                it.returnToInitPos()
            }
            it.update()
            it.edges()
            it.show(this)
        }
    }
}

@Composable
private fun createImageBitmap(): ImageBitmap {
    return ImageBitmap.imageResource(id = R.drawable.statue6)
}
