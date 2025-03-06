package dev.dubsky.aiko

import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.resources.Res
import dev.dubsky.aiko.screens.Composer
import java.awt.Toolkit
import dev.dubsky.aiko.resources.logo
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    println("Current Mode: ${ConfigManager.config.Mode}")
    println("Logging Enabled: ${ConfigManager.config.Logging}")
    var screenSize = Toolkit.getDefaultToolkit().screenSize
    var screenWidth = if (ConfigManager.config.Mode == "WQHD") 1920.dp else 1280.dp
    var screenHeight = if (ConfigManager.config.Mode == "WQHD") 1080.dp else 720.dp
    var windowState = WindowState(
        placement = WindowPlacement.Floating,
        size = DpSize(screenWidth, screenHeight),
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Aiko",
        undecorated = true,
        resizable = true,
        state = windowState,
        onKeyEvent = {
            if (it.key == Key.X && it.isCtrlPressed) {
                exitApplication()
                true
            } else {
                false
            }
        },
        icon = painterResource(resource = Res.drawable.logo)
    ) {
        WindowDraggableArea {
            Composer(windowState)
        }
    }
}