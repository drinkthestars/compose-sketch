package com.drinkstars.composesketch.dots

import androidx.compose.runtime.Composable
import com.drinkstars.composesketch.HomeScreens
import com.drinkstars.composesketch.Screen
import com.drinkstars.composesketch.Sketches

private val DotsScreens = listOf(
    Screen("MonotoneNoisyUVMesh") { MonotoneNoisyUVMesh() },
    Screen("MonotoneNoisyXYMesh") { MonotoneNoisyXYMesh() },
    Screen("HueNoisyUVMesh") { HueNoisyUVMesh() },
    Screen("HueNoisyXYMesh") { HueNoisyXYMesh() },
    Screen("NoisyXYRectMesh") { NoisyXYRectMesh() },
    Screen("NoisyXYRandomRectMesh") { NoisyXYRandomRectMesh() },
    Screen("NoisyPoints") { NoisyPoints() },
    Screen("Random2DDots") { Random2DDots() },
    Screen("Noise2DGrowingDots") { Noise2DGrowingDots() },
    Screen("Noise3DGrowingDots") { Noise3DGrowingDots() },
    Screen("Random2DGrowingDots") { Random2DGrowingDots() },
    Screen("DotStaticYRadiusVariation") { DotStaticYRadiusVariation() },
    Screen("DotStaticXRadiusVariation") { DotStaticXRadiusVariation() },
    Screen("DotsStaticXYRadiusVariation") { DotsStaticXYRadiusVariation() },
    Screen("DotYPendulum") { DotYPendulum() },
    Screen("DotYSpread") { DotYSpread() },
    Screen("DotSinRadiusVariation") { DotSinRadiusVariation() },
    Screen("DotAnimatedRadiusAndCenterVariation") { DotAnimatedRadiusAndCenterVariation() },
    Screen("Dot2DNoiseRadius") { Dot2DNoiseRadius() },
    Screen("Dot4DNoiseOffset") { Dot4DNoiseOffset() },
    Screen("Lines2DNoise") { Lines2DNoise() },
    Screen("Lines3DNoise") { Lines3DNoise() },
    Screen("Lines4DNoise") { Lines4DNoise() },
    Screen("DotAnimatedRadiusAndOffset") { DotAnimatedRadiusAndOffset() },
    Screen("DotParametric") { DotParametric() },
    Screen("DotsAroundCircleWavy") { DotsAroundCircleWavy() },
    Screen("DotsAroundCircleHalftones") { DotsAroundCircleHalftones() }
)

@Composable
fun Dots() {
    Sketches(
        home = HomeScreens.Dots,
        screens = DotsScreens
    )
}
