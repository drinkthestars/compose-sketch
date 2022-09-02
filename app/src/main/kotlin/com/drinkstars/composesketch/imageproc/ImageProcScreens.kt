package com.drinkstars.composesketch.imageproc

import androidx.compose.runtime.Composable
import com.drinkstars.composesketch.HomeScreens
import com.drinkstars.composesketch.Screen
import com.drinkstars.composesketch.Sketches

private val ImageProcScreens = listOf(
    Screen("ImageSampling") { ImageSampling() },
    Screen("ImageSamplingGestures") { ImageSamplingGestures() },
)

@Composable
fun ImageProc() {
    Sketches(
        home = HomeScreens.ImageProc,
        screens = ImageProcScreens
    )
}
