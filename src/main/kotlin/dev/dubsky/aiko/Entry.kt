package dev.dubsky.aiko

import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import dev.dubsky.aiko.screens.Composer
import java.awt.Toolkit

fun main() = application {
    var screenSize = Toolkit.getDefaultToolkit().screenSize
    var screenWidth = if (screenSize.width >= 2560) 1920.dp else 1280.dp
    var screenHeight = if (screenSize.height >= 1440) 1080.dp else 720.dp

    Window(
        onCloseRequest = ::exitApplication,
        title = "Aiko",
        undecorated = true,
        resizable = true,
        state = WindowState(
            placement = WindowPlacement.Floating,
            size = DpSize(screenWidth, screenHeight),
        ),
        onKeyEvent = {
            if (it.key == Key.X && it.isCtrlPressed) {
                exitApplication()
                true
            }
            else {
                false
            }
        }
    ) {
        WindowDraggableArea {
            Composer()
        }
    }
}
