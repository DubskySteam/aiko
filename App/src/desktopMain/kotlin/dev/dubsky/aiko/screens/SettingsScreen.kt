package dev.dubsky.aiko.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.config.ConfigManager
import java.awt.Toolkit

@Composable
fun SettingsScreen(windowState: WindowState) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = {
                ConfigManager.setMode("WQHD")
                ConfigManager.saveConfig()
                windowState.size = DpSize(1920.dp, 1080.dp)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Using WQHD (2540x1440)")
        }
        Button(
            onClick = {
                ConfigManager.setMode("FHD")
                ConfigManager.saveConfig()
                windowState.size = DpSize(1280.dp, 720.dp)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Using FHD (1920x1080)")
        }
    }
}