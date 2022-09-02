package com.drinkstars.composesketch

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun Sketches(
    home: String,
    screens: List<Screen>,
    modifier: Modifier = Modifier
) {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    val navController = rememberNavController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons,
            isNavigationBarContrastEnforced = false,
            transformColorForLightContent = { _ -> Color.Transparent }
        )
    }

    MaterialTheme {
        Scaffold(modifier = modifier.fillMaxSize()) {
            NavHost(
                modifier = Modifier.padding(it),
                navController = navController,
                startDestination = home
            ) {
                composable(home) {
                    SketchList(
                        screens = screens,
                        onClick = { screen -> navController.navigate(screen.title) }
                    )
                }
                screens.forEach { screen ->
                    composable(screen.title) { screen.content() }
                }
            }
        }
    }
}

@Composable
private fun SketchList(
    screens: List<Screen>,
    onClick: (Screen) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Spacer(Modifier.height(48.dp))
        }
        items(screens, key = { it.title }) { screen ->
            Text(
                modifier = Modifier
                    .background(MaterialTheme.colors.background)
                    .fillMaxWidth()
                    .clickable { onClick(screen) }
                    .padding(16.dp),
                text = screen.title,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h4
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
        item {
            Spacer(Modifier.height(48.dp))
        }
    }
}

data class Screen(val title: String, val content: @Composable () -> Unit)
