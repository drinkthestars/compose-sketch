package com.drinkstars.composesketch.ui

import androidx.compose.runtime.Composable
import com.drinkstars.composesketch.HomeScreens
import com.drinkstars.composesketch.LoginScreen
import com.drinkstars.composesketch.Screen
import com.drinkstars.composesketch.Sketches

private val UIExamplesScreens = listOf(
    Screen("LoginScreen") { LoginScreen() },

    )

@Composable
fun UIExamples() {
    Sketches(
        home = HomeScreens.UIExamples,
        screens = UIExamplesScreens
    )
}
