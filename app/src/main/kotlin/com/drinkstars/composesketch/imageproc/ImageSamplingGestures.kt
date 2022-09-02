package com.drinkstars.composesketch.imageproc

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.drinkstars.composesketch.R
import com.drinkstars.composesketch.capture.captureAndShare
import com.drinkstars.composesketch.map
import com.drinkstars.composesketch.sketch.Sketch
import glm_.glm
import glm_.glm.linearRand
import glm_.glm.perlin
import glm_.vec2.Vec2
import kotlin.math.cos
import kotlin.math.sin

private const val TilesX = 80f

@Composable
fun ImageSamplingGestures() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        val imageBitmap = createImageBitmap()
        var size by remember { mutableStateOf(IntSize.Zero) }
        var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
        val context = LocalContext.current

        Spacer(modifier = Modifier.height(100.dp))
        Button(onClick = {
            captureAndShare(
                width = size.width,
                height = size.height,
                rect = boundsInWindow.toAndroidRect(),
                context = context
            )
        }) {
            Text("Capture")
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(imageBitmap, modifier = Modifier
                .onGloballyPositioned {
                    size = it.size
                    boundsInWindow = it.boundsInWindow()
                }
            )
        }
    }
}

@Composable
private fun Image(imageBitmap: ImageBitmap, modifier: Modifier = Modifier) {
    val offset = remember { mutableStateOf(Offset.Zero) }

    val pixelMap = imageBitmap.toPixelMap(
        width = imageBitmap.width, height = imageBitmap.height
    )
    val canvasSize = with(LocalDensity.current) {
        DpSize(imageBitmap.width.toDp(), imageBitmap.height.toDp())
    }
    val tileSize: Float = imageBitmap.width.toFloat() / TilesX

    val rectSize = Size(imageBitmap.width.toFloat() + tileSize / 2, 300f)
    val rectOffset = Offset(-tileSize / 2, linearRand(0f, 200f))
//    val rect = Rect(offset = rectOffset, size = rectSize)

    val rectSize2 = Size(imageBitmap.width.toFloat() + tileSize / 2, 200f)
    val rectOffset2 = Offset(-tileSize / 2, linearRand(250f, 500f))
//    val rect2 = Rect(offset = rectOffset2, size = rectSize2)

    val rectSize3 = Size(imageBitmap.width.toFloat() + tileSize / 2, 300f)
    val rectOffset3 = Offset(-tileSize / 2, linearRand(600f, imageBitmap.height.toFloat()))
//    val rect3 = Rect(offset = rectOffset3, size = rectSize3)

    Sketch(modifier = modifier
        .size(canvasSize)
        .pointerInput(Unit) {
            detectDragGestures { _, dragAmount ->
                val original = offset.value
                val summed = original + dragAmount
                val newValue = Offset(
                    x = summed.x.coerceIn(0f, imageBitmap.width.toFloat()),
                    y = summed.y.coerceIn(0f, imageBitmap.height.toFloat())
                )
                offset.value = newValue
            }
        }
    ) { time ->
        val hue = map(
            value = offset.value.y,
            sourceMin = 0f,
            sourceMax = imageBitmap.height.toFloat(),
            destMin = 0f,
            destMax = 360f
        )

//        drawRect(
//            color = Color.hsv(hue, 0.3f, 1f),
//            topLeft = rectOffset,
//            size = rectSize
//        )
//        drawRect(
//            color = Color.hsv(hue, 0.3f, 1f),
//            topLeft = rectOffset2,
//            size = rectSize2
//        )
//        drawRect(
//            color = Color.hsv(hue, 0.3f, 1f),
//            topLeft = rectOffset3,
//            size = rectSize3
//        )
        for (x in 0 until imageBitmap.width step tileSize.toInt()) {
            for (y in 0 until imageBitmap.height step tileSize.toInt()) {
                val pixel = pixelMap[x, y]

//                drawRectTrueColor(x, y, tileSize, pixel)
//                drawCircleShadow(pixel, tileSize, x, y)
//                drawCircleCustomHue(
//                    height = imageBitmap.height.toFloat(),
//                    offsetY = offsetY,
//                    pixel = pixel,
//                    tileSize = tileSize,
//                    x = x,
//                    y = y
//                )

                val luminance = pixel.luminance()
                drawCircleCustomHue(
                    hue = hue,
                    luminance = luminance,
                    tileSize = tileSize,
//                    time = time,
                    x = x,
                    y = y
                )
//                if (y > rect.top && y < rect.bottom
//                    || y > rect2.top && y < rect2.bottom
//                    || y > rect3.top && y < rect3.bottom
//                ) {
//                    drawCircleTrueColor(
//                        pixel = pixel,
//                        tileSize = (luminance * tileSize / 2) + 14f,
//                        x = x,
//                        y = y
//                    )
//                } else {
//                    drawMovingCircleCustomHue(
//                        hue = hue,
//                        luminance = luminance,
//                        tileSize = tileSize + 13f,
//                        time = time,
//                        x = x,
//                        y = y
//                    )
//                }

//                val xAbsDist = (offset.value.x - x).absoluteValue
//                val yAbsDist = (offset.value.y - y).absoluteValue
//                var newX = x
//                var newY = y
//                if (xAbsDist > 1 && xAbsDist < 2 && yAbsDist > 1 && yAbsDist < 2) {
//                    newX = x + 20
//                    newY = y + 20
//                }
//                drawRectTrueColor(
//                    pixel = pixel,
//                    tileSize = tileSize,
//                    x = x,
//                    y =  y,
//                )
            }
        }

    }
}

private fun DrawScope.drawCircleShadow(
    pixel: Color,
    tileSize: Float,
    x: Int,
    y: Int
) {
    val luminance = pixel.luminance()
    drawCircle(
        color = Color.Black.copy(alpha = luminance),
        radius = (luminance * tileSize / 2) + 4f,
        center = Offset(x.toFloat() + 3f, y.toFloat() + 3f)
    )
}

private fun DrawScope.drawRectTrueColor(
    x: Int,
    y: Int,
    tileSize: Float,
    pixel: Color
) {
    drawRect(
        topLeft = Offset(x.toFloat(), y.toFloat()),
        size = Size(tileSize, tileSize),
        color = pixel
    )
}

private fun DrawScope.drawCircleCustomHue(
    height: Float,
    offsetY: Float,
    pixel: Color,
    tileSize: Float,
    x: Int,
    y: Int
) {
    val luminance = pixel.luminance()
    val hue = map(
        value = offsetY,
        sourceMin = 0f,
        sourceMax = height,
        destMin = 0f,
        destMax = 360f
    )
    drawCircle(
        color = Color.hsv(hue, (luminance + 0.2f).coerceAtMost(1f), 1f),
        radius = (luminance * tileSize / 2) + 4f,
        center = Offset(x.toFloat(), y.toFloat())
    )
}

private fun DrawScope.drawRectCustomHue(
    height: Float,
    offsetY: Float,
    pixel: Color,
    tileSize: Float,
    x: Int,
    y: Int
) {
    val luminance = pixel.luminance()
    val hue = map(
        value = offsetY,
        sourceMin = 0f,
        sourceMax = height,
        destMin = 0f,
        destMax = 360f
    )

    drawRect(
        topLeft = Offset(x.toFloat(), y.toFloat()),
        size = Size(tileSize, tileSize),
        color = Color.hsv(hue, (luminance + 0.2f).coerceAtMost(1f), 1f)
    )
}


private fun DrawScope.drawRectCustomHue(
    hue: Float,
    luminance: Float,
    tileSize: Float,
    x: Int,
    y: Int
) {
    drawRect(
        topLeft = Offset(x.toFloat(), y.toFloat()),
        size = Size(tileSize, tileSize),
        color = Color.hsv(hue, (luminance + 0.2f).coerceAtMost(1f), 1f)
    )
}

private fun DrawScope.drawCircleCustomHue(
    hue: Float,
    luminance: Float,
    tileSize: Float,
    x: Int,
    y: Int
) {
    drawCircle(
        color = Color.hsv(hue, (luminance).coerceAtMost(1f), 1f),
        radius = (luminance * 105 / 2) ,
        center = Offset(x.toFloat(), y.toFloat()),
    )

    // last batch bust
//    drawCircle(
//        color = Color.hsv(hue, (luminance + 0.2f).coerceAtMost(1f), 1f),
//        radius = (luminance * tileSize / 2) + 3f,
//        center = Offset(x.toFloat(), y.toFloat()),
//    )
}


private fun DrawScope.drawMovingCircleCustomHue(
    hue: Float,
    luminance: Float,
    time: Float,
    tileSize: Float,
    radius: Float = (luminance * tileSize / 2) + 4f,
    x: Int,
    y: Int
) {
    val randMinCenter = perlin(Vec2(x, time * 2f)) * 10f
    drawCircle(
        color = Color.hsv(hue, (luminance + 0.2f).coerceAtMost(1f), 1f),
        radius = radius,
        center = Offset(x.toFloat() + randMinCenter, y.toFloat() + randMinCenter)
    )
}

private fun DrawScope.drawGrowingCirclesCustomHue(
    hue: Float,
    luminance: Float,
    time: Float,
    tileSize: Float,
    radius: Float = (luminance * tileSize / 2) + 4f,
    x: Int,
    y: Int
) {
    drawCircle(
        color = Color.hsv(hue, (luminance + 0.2f).coerceAtMost(1f), 1f),
        radius = map(sin(luminance * 10f + time * 20f), -1f, 1f, 8f, radius),
        center = Offset(x.toFloat(), y.toFloat())
    )
}

private fun DrawScope.drawRotatedLines(
    hue: Float,
    luminance: Float,
    tileSize: Float,
    radius: Float = (luminance * tileSize / 2) + 14f,
    time: Float,
    x: Int,
    y: Int
) {
    val rotRad = 360f * glm.PIf / 180f
    val angle = map(sin(luminance + time * 10f), -1f, 1f, 0f, rotRad)
    val endx = (radius * sin(angle)) + x.toFloat()
    val endy = (radius * cos(angle)) + y.toFloat()
    drawLine(
        start = Offset(x.toFloat(), y.toFloat()),
        end = Offset(endx, endy),
        strokeWidth = 12f,
        color = Color.hsv(hue, (luminance + 0.2f).coerceAtMost(1f), 1f)
    )
}

private fun DrawScope.drawCircleTrueColor(
    pixel: Color,
    tileSize: Float,
    x: Int,
    y: Int
) {
    drawCircle(
        color = pixel,
        radius = tileSize / 2,
        center = Offset(x.toFloat(), y.toFloat())
    )
}

@Composable
private fun createImageBitmap(): ImageBitmap {
    return ImageBitmap.imageResource(id = R.drawable.statue2)
}
