package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import dev.dubsky.aiko.components.bar.ControlBar
import dev.dubsky.aiko.components.bar.NavBar
import dev.dubsky.aiko.data.Anime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun Composer(windowState: WindowState) {
    var screenActive by remember { mutableStateOf(Screens.Browse) }
    var selectedAnime by remember { mutableStateOf<Anime?>(null) }
    val windowState by remember { mutableStateOf(windowState) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            //color = 11151c
            .background(Color(0xFF11151C))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            ControlBar(
                windowState = windowState,
                onMinimizeClick = {
                    windowState.isMinimized = true
                },
                onMaximizeClick = {
                },
                onCloseClick = {
                    System.exit(0)
                }
            )

            when (screenActive) {
                Screens.Home -> {
                    HomeScreen(
                        onAnimeSelected = {
                            selectedAnime = it
                            screenActive = Screens.Anime
                        }
                    )
                }
                Screens.Anime -> {
                    selectedAnime?.let { AnimeScreen(anime = it) }
                }
                Screens.Browse -> BrowseScreen(
                    onAnimeSelected = {
                        selectedAnime = it
                        screenActive = Screens.Anime
                    }
                )
                Screens.Player -> ComingScreen()
                Screens.List -> ComingScreen()
                Screens.Settings -> SettingsScreen(windowState)
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            NavBar(
                currentScreen = screenActive,
                onScreenSelected = { screenActive = it },
                menuSize = 56.dp
            )
        }
    }
}