package com.drinkstars.composesketch.particles

import androidx.compose.runtime.Composable
import com.drinkstars.composesketch.HomeScreens
import com.drinkstars.composesketch.Screen
import com.drinkstars.composesketch.Sketches

private val ParticlesScreens = listOf(
    Screen("Attractor") { Attractor() },
    Screen("RasterizeAttractor") { RasterizeAttractor() },
    Screen("FlowField") { FlowField() },
    Screen("Constellation") { Constellation() }
)

@Composable
fun Particles() {
    Sketches(
        home = HomeScreens.Particles,
        screens = ParticlesScreens
    )
}
