package dev.dubsky.aiko.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.api.auth.anilist.do_auth
import dev.dubsky.aiko.components.button.ConfirmDialogButton
import dev.dubsky.aiko.components.button.CustomDialog
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger

@Composable
fun SettingsScreen(
    windowState: WindowState,
    navigateToLogs: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(
            onClick = {
                Logger.log(LogLevel.INFO, "SettingsScreen", "Attempting to set resolution > WQHD")
                ConfigManager.setMode("WQHD")
                ConfigManager.saveConfig()
                windowState.size = DpSize(1920.dp, 1080.dp)
                Logger.log(LogLevel.INFO, "SettingsScreen", "Attempting to set resolution > WQHD")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Using WQHD (2540x1440)")
        }
        Button(
            onClick = {
                Logger.log(LogLevel.INFO, "SettingsScreen", "Attempting to set resolution > FHD")
                ConfigManager.setMode("FHD")
                ConfigManager.saveConfig()
                windowState.size = DpSize(1280.dp, 720.dp)
                Logger.log(LogLevel.INFO, "SettingsScreen", "Set resolution to FHD")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Using FHD (1920x1080)")
        }
        ConfirmDialogButton(
            buttonText = "Authenticate me hard!",
            dialogTitle = "AniList Authentication",
            dialogMessage = "This will open a browser window to authenticate with AniList",
            confirmText = "Authenticate",
            onConfirm = { do_auth() },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                navigateToLogs()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View logs")
        }

    }
}