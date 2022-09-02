package com.drinkstars.composesketch.shaders

import androidx.compose.runtime.Composable
import com.drinkstars.composesketch.HomeScreens
import com.drinkstars.composesketch.Screen
import com.drinkstars.composesketch.Sketches

private val ShaderScreens = listOf(
    Screen("GradientShader") { GradientShader() },
    Screen("StarNestShader") { StarNestShader() },
    Screen("FractalShader") { FractalShader() },
    Screen("BlobsShader") { BlobsShader() },
    Screen("NebulaShader") { NebulaShader() },
)

@Composable
fun Shaders() {
    Sketches(
        home = HomeScreens.Shaders,
        screens = ShaderScreens
    )
}
