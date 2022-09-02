package com.drinkstars.composesketch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.drinkstars.composesketch.dots.Dots
import com.drinkstars.composesketch.drawing.Drawing
import com.drinkstars.composesketch.imageproc.ImageProc
import com.drinkstars.composesketch.osc.Oscillations
import com.drinkstars.composesketch.particles.Particles
import com.drinkstars.composesketch.shaders.Shaders
import com.drinkstars.composesketch.ui.UIExamples

object HomeScreens {
    const val Home = "Home"
    const val Oscillations = "Oscillations"
    const val Dots = "Dots"
    const val Drawing = "Drawing"
    const val ImageProc = "Image Proc"
    const val Particles = "Particles"
    const val Shaders = "Shaders"
    const val UIExamples = "UIExamples"
}

private val TopLevelScreens = listOf(
    Screen("Oscillations") { Oscillations() },
    Screen("Dots") { Dots() },
    Screen("Drawing") { Drawing() },
    Screen("ImageProc") { ImageProc() },
    Screen("Particles") { Particles() },
    Screen("Shaders") { Shaders() },
    Screen("UIExamples") { UIExamples() }
)

class SketchesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            Sketches(
                home = HomeScreens.Home,
                screens = TopLevelScreens
            )
        }
    }
}
