package dev.dubsky.aiko.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.components.bar.ControlBar
import dev.dubsky.aiko.components.bar.NavBar

@Preview
@Composable
fun Composer() {
    var screenActive by remember { mutableStateOf(Screens.Home) }
    val windowState by remember { mutableStateOf(WindowState()) }

    Column {
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
            Screens.Home -> HomeScreen()
            Screens.Browse -> ComingScreen()
            Screens.Player -> ComingScreen()
            Screens.List -> ComingScreen()
            Screens.Settings -> ComingScreen()
        }

        NavBar(
            currentScreen = screenActive,
            onScreenSelected = { screenActive = it },
            menuSize = 56.dp
        )
    }
}