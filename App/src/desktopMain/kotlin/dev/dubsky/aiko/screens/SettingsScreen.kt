package dev.dubsky.aiko.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.api.auth.anilist.do_auth
import dev.dubsky.aiko.config.ConfigManager

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
        Button(
            onClick = {
                do_auth()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login / Re-auth [Anilist]")
        }
    }
}