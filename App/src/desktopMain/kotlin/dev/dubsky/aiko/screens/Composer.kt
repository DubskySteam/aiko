package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.components.bar.UnifiedBar
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.data.Anime
import dev.dubsky.aiko.theme.AppThemedContent
import dev.dubsky.aiko.theme.ThemeManager
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.system.exitProcess

@Preview
@Composable
fun Composer(windowState: WindowState) {
    var screenActive by remember { mutableStateOf(Screens.List) }
    var selectedAnime by remember { mutableStateOf<Anime?>(null) }
    val windowState by remember { mutableStateOf(windowState) }

    var themeState = remember { mutableStateOf(ConfigManager.config.Theme) }

    ThemeManager.currentTheme = themeState

    AppThemedContent {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                UnifiedBar(currentScreen = screenActive, onMinimizeClick = {
                    windowState.isMinimized = true
                }, onMaximizeClick = {
                    if (windowState.placement == WindowPlacement.Maximized) {
                        windowState.placement = WindowPlacement.Floating
                    }
                    else {
                        windowState.placement = WindowPlacement.Maximized
                    }
                }, onCloseClick = {
                    exitProcess(0)
                }, onScreenSelected = { screenActive = it })

                when (screenActive) {
                    Screens.Home -> {
                        HomeScreen(
                            onAnimeSelected = {
                                selectedAnime = it
                                screenActive = Screens.Anime
                            })
                    }

                    Screens.Anime -> {
                        selectedAnime?.let { AnimeScreen(anime = it) }
                    }

                    Screens.Browse -> BrowseScreen(
                        onAnimeSelected = {
                            selectedAnime = it
                            screenActive = Screens.Anime
                        })

                    Screens.PROFILE -> ProfileScreen()
                    Screens.List -> AnimeListScreen()
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