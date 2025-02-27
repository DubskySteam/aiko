package dev.dubsky.aiko

import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.dubsky.aiko.screens.Composer

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Aiko",
        undecorated = true,
        resizable = true
    ) {
        WindowDraggableArea {
            Composer()
        }
    }
}
