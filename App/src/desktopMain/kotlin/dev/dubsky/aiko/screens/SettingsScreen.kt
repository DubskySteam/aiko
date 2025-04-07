package dev.dubsky.aiko.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import dev.dubsky.aiko.theme.getColorSchemeByEnum

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
            Column() {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = windowState.size.width == 1280.dp && windowState.size.height == 720.dp,
                        onClick = {
                            windowState.size = DpSize(1280.dp, 720.dp)
                            ConfigManager.setResolution("FHD")
                            ConfigManager.saveConfig()
                        },
                    )
                    Text("Floating Window (1280x720)")
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = windowState.size.width == 1920.dp && windowState.size.height == 1080.dp,
                        onClick = {
                            windowState.size = DpSize(1920.dp, 1080.dp)
                            ConfigManager.setResolution("WQHD")
                            ConfigManager.saveConfig()
                        },
                    )
                    Text("Floating Window (1920x1080)")
                }
            }
        }

        SettingsCategory(title = "Theme") {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
            AppTheme.entries.forEachIndexed { index, entry ->
                Button(
                    onClick = {
                        updateTheme(entry)
                        ConfigManager.setTheme(entry)
                        ConfigManager.saveConfig()
                    },
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getColorSchemeByEnum(entry).secondary),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 5.dp,
                        pressedElevation = 0.dp,
                        hoveredElevation = 10.dp
                    ),
                    modifier = Modifier
                        .size(40.dp)
                ) {}
            }

            }
        }

        SettingsCategory(title = "NSFW Content") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Switch to adult content?",
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = isAdultEnabled,
                    onCheckedChange = {
                        isAdultEnabled = it
                        ConfigManager.setAdult(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .height(40.dp)
                )
            }
        }

        SettingsCategory(title = "Logging") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { navigateToLogs() },
                    modifier = Modifier
                        .height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) { Icon(Icons.Default.Terminal, null)
                        Text("View Logs")
                    }
                }
                Switch(
                    checked = isLoggingEnabled,
                    onCheckedChange = {
                        isLoggingEnabled = it
                        ConfigManager.setLogging(it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        uncheckedThumbColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .height(40.dp)
                )
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
                        label = { Text(if (entry) "Enabled" else "Disabled") },
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