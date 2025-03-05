package dev.dubsky.aiko.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.WindowState
import java.awt.Toolkit

@Composable
fun LabeledSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = label, style = MaterialTheme.typography.h6)
        Slider(value = value, onValueChange = onValueChange, valueRange = 0f..1f, steps = 99)
    }
}

@Composable
fun SettingsScreen(windowState: WindowState) {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val maxWidth = screenSize.width.dp
    val maxHeight = screenSize.height.dp
    val minWidth = (screenSize.width * 0.3).dp
    val minHeight = (screenSize.height * 0.3).dp

    var windowSize by remember { mutableStateOf(0.5f) }

    LaunchedEffect(windowSize) {
        val newWidth = minWidth + (maxWidth - minWidth) * windowSize
        val newHeight = newWidth * 9 / 16
        windowState.size = DpSize(newWidth, newHeight)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(modifier = Modifier.weight(1f)) {
                LabeledSlider("Window Size", windowSize) { windowSize = it }
                repeat(2) { LabeledSlider("Slider ${it + 2}", 0.5f) {} }
            }
            Column(modifier = Modifier.weight(1f)) {
                repeat(3) { LabeledSlider("Slider ${it + 4}", 0.5f) {} }
            }
            Column(modifier = Modifier.weight(1f)) {
                repeat(3) { LabeledSlider("Slider ${it + 7}", 0.5f) {} }
            }
        }
    }
}