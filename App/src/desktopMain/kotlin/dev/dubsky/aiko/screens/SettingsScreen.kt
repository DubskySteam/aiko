package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.api.auth.anilist.do_auth
import dev.dubsky.aiko.components.button.ConfirmDialogButton
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import dev.dubsky.aiko.theme.AikoDefaults
import dev.dubsky.aiko.theme.AppTheme

@Composable
fun SettingsScreen(
    windowState: WindowState,
    navigateToLogs: () -> Unit,
    updateTheme: (AppTheme) -> Unit,
    currentTheme: AppTheme
) {
    var isLoggingEnabled by remember { mutableStateOf(ConfigManager.config.logging) }
    var isAutoUpdateEnabled by remember { mutableStateOf(ConfigManager.config.autoUpdate) }
    var isAdultEnabled by remember { mutableStateOf(ConfigManager.config.adult) }
    var proxy by remember { mutableStateOf(ConfigManager.config.proxy) }
    var apiUrl by remember { mutableStateOf(ConfigManager.config.api) }
    var referUrl by remember { mutableStateOf(ConfigManager.config.refer) }

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
                        ConfigManager.setResolution("WQHD")
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
                        ConfigManager.setResolution("FHD")
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
                        colors = AikoDefaults.segmentedButtonColors
                    )
                }
            }
        }

        SettingsCategory(title = "Content") {
            Text(
                text = "Switch to adult content? A hybrid between adult and non is not yet supported.",
                fontWeight = FontWeight.Bold,
            )
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            SingleChoiceSegmentedButtonRow {
                listOf(true, false).forEachIndexed { index, entry ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = 2
                        ),
                        onClick = {
                            isAdultEnabled = entry
                            ConfigManager.setAdult(entry)
                        },
                        selected = isAdultEnabled == entry,
                        label = { Text(entry.toString()) },
                        colors = AikoDefaults.segmentedButtonColors
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
                        colors = AikoDefaults.segmentedButtonColors
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

        SettingsCategory(title = "Player - API & Proxy") {
            Text(
                text = "If you have questions about what to put here, please refer to the tutorial in the Discord server.",
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            TextField(
                value = apiUrl,
                onValueChange = {
                    apiUrl = it
                    ConfigManager.setApi(it)
                },
                label = { Text("API URL") },
                placeholder = { Text("127.0.0.1") },
                modifier = Modifier.fillMaxWidth()
            )
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
            TextField(
                value = referUrl,
                onValueChange = {
                    referUrl = it
                    ConfigManager.setRefer(it)
                },
                label = { Text("Refer URL") },
                placeholder = { Text("") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            Text(
                text = "Automatically update your watchlist when you watch an episode?"
            )
            Spacer(
                modifier = Modifier.height(2.dp)
            )
            SingleChoiceSegmentedButtonRow {
                listOf(true, false).forEachIndexed { index, entry ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = 2
                        ),
                        onClick = {
                            isAutoUpdateEnabled = entry
                            ConfigManager.setAutoUpdate(entry)
                        },
                        selected = isAutoUpdateEnabled == entry,
                        label = { Text(entry.toString()) },
                        colors = AikoDefaults.segmentedButtonColors
                    )
                }
            }
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
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp)),
        colors = AikoDefaults.cardColors
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}