package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.components.bar.UnifiedBar
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.data.Anime
import dev.dubsky.aiko.theme.AikoTheme
import dev.dubsky.aiko.theme.ThemeManager
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.system.exitProcess

@Preview
@Composable
fun Composer(windowState: WindowState) {
    var screenActive by remember { mutableStateOf(Screens.Home) }
    var selectedAnime by remember { mutableStateOf<Anime?>(null) }
    val windowState by remember { mutableStateOf(windowState) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var themeState = remember { mutableStateOf(ConfigManager.config.theme) }

    ThemeManager.currentTheme = themeState

    AikoTheme {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
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