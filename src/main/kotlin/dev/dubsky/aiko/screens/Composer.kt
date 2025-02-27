package dev.dubsky.aiko.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.components.bar.ControlBar

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
            Screens.Settings -> ComingScreen()
        }

    }
}