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
import androidx.compose.ui.graphics.drawscope.translate
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
import glm_.value
import kotlin.math.sin

private const val TilesX = 90f

@Composable
fun ImageSampling() {
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

/**
 * Sample showing how to obtain a [PixelMap] to query pixel information
 * from an underlying [ImageBitmap]
 */
@Composable
private fun Image(imageBitmap: ImageBitmap, modifier: Modifier = Modifier) {
    var offsetY by remember { mutableStateOf(TilesX) }

    val pixelMap = imageBitmap.toPixelMap(
        width = imageBitmap.width, height = imageBitmap.height
    )
    val canvasSize = with(LocalDensity.current) {
        DpSize(imageBitmap.width.toDp(), imageBitmap.height.toDp())
    }
    val dotCount: Float = imageBitmap.width.toFloat() / TilesX

    Sketch(modifier = modifier
        .size(canvasSize)
        .pointerInput(Unit) {
            detectDragGestures { _, dragAmount ->
                val originalY = offsetY.value
                val summedY = originalY + dragAmount.y
                val newValueY = summedY.coerceIn(0f, imageBitmap.height.toFloat())
                offsetY = newValueY

            }
        }
    ) { time ->
        translate(imageBitmap.width/2f, imageBitmap.height/2f) {
            for (x in 0 until imageBitmap.width step dotCount.toInt()) {
                for (y in 0 until imageBitmap.height step dotCount.toInt()) {
                    val pixel = pixelMap[x, y]

//                    val hue = map(
//                        value = offsetY,
//                        sourceMin = 0f,
//                        sourceMax = imageBitmap.height.toFloat(),
//                        destMin = 0f,
//                        destMax = 360f
//                    )
//                    drawRect(
//                        topLeft = Offset(x.toFloat(), y.toFloat()),
//                        size = Size(dotCount, dotCount),
//                        color = pixel
//                    )
//                drawRectTrueColor(x, y, dotCount, pixel)
//                drawCircleShadow(pixel, dotCount, x, y)
//                drawCircleCustomHue(
//                    height = imageBitmap.height.toFloat(),
//                    offsetY = offsetY,
//                    pixel = pixel,
//                    dotCount = dotCount,
//                    x = x,
//                    y = y
//                )
//                drawCircleTrueColor(pixel, dotCount, x, y)
                    drawZIndexMindBlowing(
                        pixel = pixel,
                        time = time,
                        y = y,
                        imageBitmap = imageBitmap,
                        offsetY = offsetY,
                        x = x,
                        dotCount = dotCount
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawZIndexOffsetSine(
    pixel: Color,
    time: Float,
    y: Int,
    imageBitmap: ImageBitmap,
    x: Int,
    dotCount: Float,
    offsetY: Float,
    hue: Float = 1f
) {
    val luminance = pixel.luminance()
    val z = map(
        value = sin(luminance + time * 2f),
        sourceMin = -1f,
        sourceMax = 1f,
        destMin = imageBitmap.width.toFloat() * 0.3f,
        destMax = imageBitmap.width.toFloat()
    )
//    val z = map(
//        value = sin(time * 3f + (y * 0.01f)),
//        sourceMin = -1f,
//        sourceMax = 1f,
//        destMin = (imageBitmap.width.toFloat() / 2 * (1 - luminance)) + 1000f, //
//        destMax = imageBitmap.width.toFloat()
//    )

    val trueX = x - imageBitmap.width / 2f
    val trueY = y - imageBitmap.height / 2f
    val sx = map(trueX / z, 0f, 1f, 0f, imageBitmap.width.toFloat())
    val sy = map(trueY / z, 0f, 1f, 0f, imageBitmap.height.toFloat())

    // HUE CIRCLES
//                    drawCircle(
//                        color = Color.hsv(hue, (luminance + 0.2f).coerceAtMost(1f), 1f),
//                        radius = (luminance * dotCount / 2) + 14f,
//                        center = Offset(sx, sy)
//                    )
    drawRect(
        topLeft = Offset(sx, sy),
        size = Size(dotCount, dotCount),
        color = pixel
    )
}

private fun DrawScope.drawZIndexOffset(
    pixel: Color,
    time: Float,
    y: Int,
    imageBitmap: ImageBitmap,
    x: Int,
    dotCount: Float,
    offsetY: Float,
    hue: Float = 1f
) {
    val luminance = pixel.luminance()
    val z = map(
        value = offsetY * luminance,
        sourceMin = 0f,
        sourceMax = imageBitmap.height.toFloat(),
        destMin = imageBitmap.width.toFloat(),
        destMax = imageBitmap.width.toFloat()/2f
    )
//    val z = map(
//        value = sin(time * 3f + (y * 0.01f)),
//        sourceMin = -1f,
//        sourceMax = 1f,
//        destMin = (imageBitmap.width.toFloat() / 2 * (1 - luminance)) + 1000f, //
//        destMax = imageBitmap.width.toFloat()
//    )

    val trueX = x - imageBitmap.width / 2f
    val trueY = y - imageBitmap.height / 2f
    val sx = map(trueX / z, 0f, 1f, 0f, imageBitmap.width.toFloat())
    val sy = map(trueY / z, 0f, 1f, 0f, imageBitmap.height.toFloat())

    // HUE CIRCLES
//                    drawCircle(
//                        color = Color.hsv(hue, (luminance + 0.2f).coerceAtMost(1f), 1f),
//                        radius = (luminance * dotCount / 2) + 14f,
//                        center = Offset(sx, sy)
//                    )
    drawRect(
        topLeft = Offset(sx, sy),
        size = Size(dotCount, dotCount),
        color = pixel
    )
}

private fun DrawScope.drawZIndexFull(
    pixel: Color,
    time: Float,
    y: Int,
    imageBitmap: ImageBitmap,
    x: Int,
    dotCount: Float,
    offsetY: Float,
    hue: Float = 1f
) {
    val luminance = pixel.luminance()
    val z = if (y < imageBitmap.height / 5) {
        map(
            value = sin(time * 3f + (y * 0.01f)),
            sourceMin = 0f,
            sourceMax = 1f,
            destMin = (x / 2 * (luminance)) + 1000f,
            destMax = imageBitmap.width.toFloat()
        )
    } else {
        imageBitmap.width.toFloat()
    }
//    val z = map(
//        value = sin(time * 3f + (y * 0.01f)),
//        sourceMin = -1f,
//        sourceMax = 1f,
//        destMin = (imageBitmap.width.toFloat() / 2 * (1 - luminance)) + 1000f, //
//        destMax = imageBitmap.width.toFloat()
//    )

    val trueX = x - imageBitmap.width / 2f
    val trueY = y - imageBitmap.height / 2f
    val sx = map(trueX / z, 0f, 1f, 0f, imageBitmap.width.toFloat())
    val sy = map(trueY / z, 0f, 1f, 0f, imageBitmap.height.toFloat())

    // HUE CIRCLES
//                    drawCircle(
//                        color = Color.hsv(hue, (luminance + 0.2f).coerceAtMost(1f), 1f),
//                        radius = (luminance * dotCount / 2) + 14f,
//                        center = Offset(sx, sy)
//                    )
    drawRect(
        topLeft = Offset(sx, sy),
        size = Size(dotCount, dotCount),
        color = pixel
    )
}

private fun DrawScope.drawZIndexMindBlowing(
    pixel: Color,
    time: Float,
    y: Int,
    imageBitmap: ImageBitmap,
    x: Int,
    dotCount: Float,
    offsetY: Float,
    hue: Float = 1f
) {
    val luminance = pixel.luminance()
    val z = if (y < imageBitmap.height / 5) {
         map(
            value = sin(time * 3f + (y * 0.01f)),
            sourceMin = -1f,
            sourceMax = 1f,
            destMin = (imageBitmap.width.toFloat() / 2 * (1 - luminance)) + 1000f, //
            destMax = imageBitmap.width.toFloat()
        )
    } else {
        imageBitmap.width.toFloat()
    }
//    val z = map(
//        value = sin(time * 3f + (y * 0.01f)),
//        sourceMin = -1f,
//        sourceMax = 1f,
//        destMin = (imageBitmap.width.toFloat() / 2 * (1 - luminance)) + 1000f, //
//        destMax = imageBitmap.width.toFloat()
//    )

    val trueX = x - imageBitmap.width / 2f
    val trueY = y - imageBitmap.height / 2f
    val sx = map(trueX / z, 0f, 1f, 0f, imageBitmap.width.toFloat())
    val sy = map(trueY / z, 0f, 1f, 0f, imageBitmap.height.toFloat())

    // HUE CIRCLES
//                    drawCircle(
//                        color = Color.hsv(hue, (luminance + 0.2f).coerceAtMost(1f), 1f),
//                        radius = (luminance * dotCount / 2) + 14f,
//                        center = Offset(sx, sy)
//                    )
    drawRect(
        topLeft = Offset(sx, sy),
        size = Size(dotCount, dotCount),
        color = pixel
    )
}

private fun DrawScope.drawCircleShadow(
    pixel: Color,
    dotCount: Float,
    x: Int,
    y: Int
) {
    val luminance = pixel.luminance()
    drawCircle(
        color = Color.Black.copy(alpha = luminance),
        radius = (luminance * dotCount / 2) + 4f,
        center = Offset(x.toFloat() + 3f, y.toFloat() + 3f)
    )
}

private fun DrawScope.drawRectTrueColor(
    x: Int,
    y: Int,
    dotCount: Float,
    pixel: Color
) {
    drawRect(
        topLeft = Offset(x.toFloat(), y.toFloat()),
        size = Size(dotCount, dotCount),
        color = pixel
    )
}

private fun DrawScope.drawCircleCustomHue(
    height: Float,
    offsetY: Float,
    pixel: Color,
    dotCount: Float,
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
        radius = (luminance * dotCount / 2) + 4f,
        center = Offset(x.toFloat(), y.toFloat())
    )
}

private fun DrawScope.drawCircleTrueColor(
    pixel: Color,
    dotCount: Float,
    x: Int,
    y: Int
) {
    drawCircle(
        color = pixel,
        radius = dotCount / 2,
        center = Offset(x.toFloat(), y.toFloat())
    )
}

@Composable
private fun createImageBitmap(): ImageBitmap {
    return ImageBitmap.imageResource(id = R.drawable.statue2)
}
