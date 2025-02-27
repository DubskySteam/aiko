package dev.dubsky.aiko.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.api.AnimeData
import dev.dubsky.aiko.components.bar.ControlBar
import dev.dubsky.aiko.components.bar.NavBar

@Preview
@Composable
fun Composer() {
    var screenActive by remember { mutableStateOf(Screens.Home) }
    val windowState by remember { mutableStateOf(WindowState()) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ControlBar(
                windowState = windowState,
                onMinimizeClick = {
                    windowState.isMinimized = true
                },
                onMaximizeClick = {
                },
                onCloseClick = {
                    System.exit(0)
                }
            )

            when (screenActive) {
                Screens.Home -> {
                    HomeScreen()
                }
                Screens.Browse -> ComingScreen()
                Screens.Player -> ComingScreen()
                Screens.List -> ComingScreen()
                Screens.Settings -> SettingsScreen()
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            NavBar(
                currentScreen = screenActive,
                onScreenSelected = { screenActive = it },
                menuSize = 56.dp
            )
        }
    }
}