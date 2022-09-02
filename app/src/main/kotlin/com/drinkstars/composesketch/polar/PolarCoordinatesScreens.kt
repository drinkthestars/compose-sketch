package com.drinkstars.composesketch.polar

import androidx.compose.runtime.Composable
import com.drinkstars.composesketch.HomeScreens
import com.drinkstars.composesketch.PolygonsColor
import com.drinkstars.composesketch.PolygonsComplex
import com.drinkstars.composesketch.PolygonsSimple
import com.drinkstars.composesketch.Screen
import com.drinkstars.composesketch.Sketches

private val PolarCoordinatesScreens = listOf(
    Screen("PolygonsColor") { PolygonsColor() },
    Screen("PolygonsSimple") { PolygonsSimple() },
    Screen("PolygonsComplex") { PolygonsComplex() },
    Screen("BlobbyLoop") { BlobbyLoop() },
    Screen("Blobby") { Blobby() },
)

@Composable
fun PolarCoordinates() {
    Sketches(
        home = HomeScreens.PolarCoordinates,
        screens = PolarCoordinatesScreens
    )
}
