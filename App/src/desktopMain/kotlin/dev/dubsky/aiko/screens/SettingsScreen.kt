package dev.dubsky.aiko.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.api.auth.anilist.do_auth
import dev.dubsky.aiko.components.button.ConfirmDialogButton
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import dev.dubsky.aiko.theme.AppTheme

@Composable
fun SettingsScreen(
    windowState: WindowState,
    navigateToLogs: () -> Unit,
    updateTheme: (AppTheme) -> Unit,
    currentTheme: AppTheme
) {
    var isLoggingEnabled by remember { mutableStateOf(ConfigManager.config.Logging) }
    var proxy by remember { mutableStateOf(ConfigManager.config.Proxy) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        SettingsCategory(title = "Display") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        Logger.log(LogLevel.INFO, "SettingsScreen", "Attempting to set resolution > WQHD")
                        ConfigManager.setMode("WQHD")
                        ConfigManager.saveConfig()
                        windowState.size = DpSize(1920.dp, 1080.dp)
                        Logger.log(LogLevel.INFO, "SettingsScreen", "Attempting to set resolution > WQHD")
                    },
                    modifier = Modifier.weight(1f)
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
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Using FHD (1920x1080)")
                }
            }
        }

        SettingsCategory(title = "Theme") {
            SingleChoiceSegmentedButtonRow {
                AppTheme.entries.forEachIndexed { index, entry ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = AppTheme.entries.size
                        ),
                        onClick = {
                            updateTheme(entry)
                            ConfigManager.setTheme(entry)
                            ConfigManager.saveConfig()
                        },
                        selected = currentTheme.name == entry.name,
                        label = { Text(entry.name) },
                        colors = SegmentedButtonColors(
                            activeContainerColor = MaterialTheme.colors.background,
                            activeContentColor = MaterialTheme.colors.primary,
                            activeBorderColor = MaterialTheme.colors.background,
                            inactiveContainerColor = MaterialTheme.colors.background,
                            inactiveContentColor = MaterialTheme.colors.onSurface,
                            inactiveBorderColor = MaterialTheme.colors.background,
                            disabledActiveContainerColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                            disabledActiveContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.38f),
                            disabledActiveBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                            disabledInactiveContainerColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                            disabledInactiveContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.38f),
                            disabledInactiveBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                        )
                    )
                }
            }
        }

        SettingsCategory(title = "Logging") {
            SingleChoiceSegmentedButtonRow {
                listOf(true, false).forEachIndexed { index, entry ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = 2
                        ),
                        onClick = {
                            isLoggingEnabled = entry
                            ConfigManager.setLogging(entry)
                        },
                        selected = isLoggingEnabled == entry,
                        label = { Text(entry.toString()) },
                        colors = SegmentedButtonColors(
                            activeContainerColor = MaterialTheme.colors.background,
                            activeContentColor = MaterialTheme.colors.primary,
                            activeBorderColor = MaterialTheme.colors.background,
                            inactiveContainerColor = MaterialTheme.colors.background,
                            inactiveContentColor = MaterialTheme.colors.onSurface,
                            inactiveBorderColor = MaterialTheme.colors.background,
                            disabledActiveContainerColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                            disabledActiveContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.38f),
                            disabledActiveBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                            disabledInactiveContainerColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                            disabledInactiveContentColor = MaterialTheme.colors.onSurface.copy(alpha = 0.38f),
                            disabledInactiveBorderColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                        )
                    )
                }
            }
            Button(
                onClick = { navigateToLogs() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View Logs")
            }
        }

        SettingsCategory(title = "Connect your watchlist") {
            ConfirmDialogButton(
                buttonText = "Authenticate with AniList",
                dialogTitle = "AniList Authentication",
                dialogMessage = "This will open a browser window to authenticate with AniList.",
                confirmText = "Authenticate",
                onConfirm = { do_auth() },
                modifier = Modifier.fillMaxWidth()
            )
        }

        SettingsCategory(title = "Proxy") {
            TextField(
                value = proxy,
                onValueChange = {
                    proxy = it
                    ConfigManager.setProxy(it)
                },
                label = { Text("Proxy URL") },
                placeholder = { Text("127.0.0.1") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SettingsCategory(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.surface,
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}