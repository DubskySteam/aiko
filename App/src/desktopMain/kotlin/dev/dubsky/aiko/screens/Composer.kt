package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.components.bar.UnifiedBar
import dev.dubsky.aiko.data.Anime
import dev.dubsky.aiko.theme.AppTheme
import dev.dubsky.aiko.theme.AppThemedContent
import dev.dubsky.aiko.theme.ThemeManager
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.system.exitProcess

@Preview
@Composable
fun Composer(windowState: WindowState) {
    var screenActive by remember { mutableStateOf(Screens.Settings) }
    var selectedAnime by remember { mutableStateOf<Anime?>(null) }
    val windowState by remember { mutableStateOf(windowState) }

    var themeState = remember { mutableStateOf(AppTheme.ORANGE) }

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
                }, onMaximizeClick = {}, onCloseClick = {
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

                    Screens.Player -> ComingScreen()
                    Screens.List -> ComingScreen()
                    Screens.Settings -> SettingsScreen(
                        windowState = windowState, navigateToLogs = { screenActive = Screens.Logs })

                    Screens.Logs -> LogViewerScreen()
                }
            }
        }

    }
}