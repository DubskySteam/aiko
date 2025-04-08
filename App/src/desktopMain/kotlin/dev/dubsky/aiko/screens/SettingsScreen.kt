package dev.dubsky.aiko.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.api.auth.anilist.do_auth
import dev.dubsky.aiko.components.button.ConfirmDialogButton
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import dev.dubsky.aiko.resources.Res
import dev.dubsky.aiko.resources.questionmark
import dev.dubsky.aiko.theme.AikoDefaults
import dev.dubsky.aiko.theme.AppTheme
import dev.dubsky.aiko.theme.getColorSchemeByEnum
import org.jetbrains.compose.resources.painterResource

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
            Spacer(
                modifier = Modifier.height(8.dp)
            )
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
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(Res.drawable.questionmark),
                    contentDescription = "API & Proxy Help",
                    modifier = Modifier
                        .size(28.dp)
                        .padding(end = 8.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Q&A on Discord",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xffff6a00)
                )
            }
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            OutlinedTextField(
                value = apiUrl,
                onValueChange = {
                    apiUrl = it
                    ConfigManager.setApi(it)
                },
                label = { Text("API URL") },
                placeholder = { Text("127.0.0.1") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedSupportingTextColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                leadingIcon = {
                    Icon(
                        Icons.Filled.Api,
                        contentDescription = "API URL",
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            OutlinedTextField(
                value = proxy,
                onValueChange = {
                    proxy = it
                    ConfigManager.setProxy(it)
                },
                label = { Text("Proxy URL", modifier = Modifier.background(Color.Transparent)) },
                placeholder = { Text("127.0.0.1") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedSupportingTextColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
                ),
                leadingIcon = {
                    Icon(
                        Icons.Filled.Router,
                        contentDescription = "Proxy URL",
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            OutlinedTextField(
                value = referUrl,
                onValueChange = {
                    referUrl = it
                    ConfigManager.setRefer(it)
                },
                label = { Text("Refer URL") },
                placeholder = { Text("") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(50.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedSupportingTextColor = MaterialTheme.colorScheme.onPrimary,
                    focusedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                leadingIcon = {
                    Icon(
                        Icons.Filled.BroadcastOnHome,
                        contentDescription = "Refer URL",
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            Spacer(
                modifier = Modifier.height(8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Auto-update after an episode?",
                    fontWeight = FontWeight.Thin,
                    modifier = Modifier.padding(end = 16.dp).width(IntrinsicSize.Max)
                )
                Switch(
                    checked = isAutoUpdateEnabled,
                    onCheckedChange = {
                        isAutoUpdateEnabled = it
                        ConfigManager.setAutoUpdate(it)
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