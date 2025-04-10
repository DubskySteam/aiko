package dev.dubsky.aiko

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import dev.dubsky.aiko.components.bar.UnifiedBar
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.data.Anime
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import dev.dubsky.aiko.resources.Res
import dev.dubsky.aiko.resources.logo
import dev.dubsky.aiko.screens.*
import dev.dubsky.aiko.theme.AikoTheme
import dev.dubsky.aiko.theme.ThemeManager
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import java.awt.Toolkit
import kotlin.system.exitProcess

fun main() = application {
    var screenSize = Toolkit.getDefaultToolkit().screenSize
    var screenWidth = if (ConfigManager.config.resolution == "WQHD") 1920.dp else 1280.dp
    var screenHeight = if (ConfigManager.config.resolution == "WQHD") 1080.dp else 720.dp
    var windowState = WindowState(
        placement = WindowPlacement.Floating,
        size = DpSize(screenWidth, screenHeight),
    )
    var screenActive by remember { mutableStateOf(Screens.Home) }
    var selectedAnime by remember { mutableStateOf<Anime?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val themeState = remember { mutableStateOf(ConfigManager.config.theme) }

    ThemeManager.currentTheme = themeState

    if (ConfigManager.config.logging) {
        Logger.log(
            LogLevel.INFO, "Entry",
            "Starting with config: " +
                    "<Resolution: ${ConfigManager.config.resolution}> " +
                    "<Logging: ${ConfigManager.config.logging}> " +
                    "<Theme: ${ConfigManager.config.theme}>  " +
                    "<Token exists: ${ConfigManager.config.authToken != ""}>"
        )
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Aiko",
        undecorated = true,
        resizable = true,
        state = windowState,
        onKeyEvent = {
            if (it.key == Key.X && it.isCtrlPressed) {
                exitApplication()
                true
            } else if (it.key == Key.S && it.isCtrlPressed) {
                if (!drawerState.isAnimationRunning) {
                    scope.launch {
                        if (drawerState.isOpen) {
                            drawerState.close()
                        } else {
                            drawerState.open()
                        }
                    }
                }
                true
            } else {
                false
            }
        },
        icon = painterResource(resource = Res.drawable.logo)
    ) {
        AikoTheme {
            Box(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    WindowDraggableArea {
                        UnifiedBar(
                            currentScreen = screenActive, onMinimizeClick = {
                                windowState.isMinimized = true
                            }, onMaximizeClick = {
                                if (windowState.placement == WindowPlacement.Maximized) {
                                    windowState.placement = WindowPlacement.Floating
                                } else {
                                    windowState.placement = WindowPlacement.Maximized
                                }
                            }, onCloseClick = {
                                exitProcess(0)
                            }, onScreenSelected = { screenActive = it },
                            onSettingsClick = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isOpen) {
                                            close()
                                        } else {
                                            open()
                                        }
                                    }
                                }
                            }
                        )
                    }

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        ModalNavigationDrawer(
                            drawerState = drawerState,
                            drawerContent = {
                                ModalDrawerSheet(
                                    modifier = Modifier
                                        .width(IntrinsicSize.Max)
                                        .shadow(
                                            elevation = 22.dp,
                                            shape = RoundedCornerShape(8.dp),
                                            ambientColor = Color.Black,
                                            spotColor = Color.Black
                                        ),
                                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                                    drawerContentColor = MaterialTheme.colorScheme.onSurface,
                                ) {
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        SettingsScreen(
                                            windowState = windowState,
                                            navigateToLogs = { screenActive = Screens.Logs },
                                            updateTheme = {
                                                themeState.value = it
                                            },
                                            currentTheme = themeState.value
                                        )
                                    }
                                }
                            },
                            scrimColor = Color.Transparent
                        ) {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                when (screenActive) {
                                    Screens.Home -> {
                                        HomeScreen(
                                            onAnimeSelected = {
                                                selectedAnime = it
                                                screenActive = Screens.Anime
                                            },
                                            onBrowseClick = {
                                                screenActive = Screens.Browse
                                            }
                                        )
                                    }

                                    Screens.Anime -> {
                                        selectedAnime?.let {
                                            AnimeScreen(
                                                anime = it,
                                                onPlayerClick = {
                                                    screenActive = Screens.PLAYER
                                                })
                                        }
                                    }

                                    Screens.Browse -> BrowseScreen(
                                        onAnimeSelected = {
                                            selectedAnime = it
                                            screenActive = Screens.Anime
                                        },
                                        windowSize = windowState.size
                                    )

                                    Screens.PROFILE -> ProfileScreen()
                                    Screens.List -> AnimeListScreen()
                                    Screens.PLAYER -> {
                                        selectedAnime?.let { anime ->
                                            PlayerScreen(anime.id, anime.title)
                                        }
                                    }

                                    Screens.Settings -> SettingsScreen(
                                        windowState = windowState,
                                        navigateToLogs = { screenActive = Screens.Logs },
                                        updateTheme = {
                                            themeState.value = it
                                        },
                                        currentTheme = themeState.value
                                    )

                                    Screens.Logs -> LogViewerScreen()
                                }
                            }
                        }

                    }
                }
            }
        }
    }
}