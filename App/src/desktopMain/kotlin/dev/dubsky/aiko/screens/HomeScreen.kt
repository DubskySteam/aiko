package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.dubsky.aiko.api.AnimeData
import dev.dubsky.aiko.api.MemCache
import dev.dubsky.aiko.components.card.AnimeCard
import dev.dubsky.aiko.config.AppVersion
import dev.dubsky.aiko.data.Anime
import dev.dubsky.aiko.graphql.type.MediaSeason
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import kotlinx.coroutines.launch
import java.awt.Desktop
import java.net.URI

@Composable
fun HomeScreen(
    onAnimeSelected: (Anime) -> Unit = {},
    onBrowseClick: () -> Unit = {},
    windowSize: DpSize
) {
    var versionCheck by remember { mutableStateOf<AppVersion.VersionCheckResult?>(null) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading.value = true
            versionCheck = AppVersion.checkForUpdates()
            if (MemCache.needsRefresh()) {
                val airingResponse = AnimeData().getTopAiringAnime()
                val seasonalResponse = AnimeData().getTopAiringAnimeBySeason()

                val allSeasonalAnime = seasonalResponse.data?.Page?.media?.mapNotNull { media ->
                    media?.let {
                        Anime(
                            id = it.id,
                            title = it.title?.english ?: it.title?.native ?: "Unknown",
                            imageUrl = it.bannerImage ?: "",
                            rating = it.averageScore ?: 0,
                            description = it.description ?: "",
                            season = it.season ?: MediaSeason.UNKNOWN__,
                            genres = it.genres?.mapNotNull { genre -> genre } ?: emptyList(),
                            coverImage = it.coverImage?.large ?: "",
                            seasonYear = it.seasonYear ?: 1500
                        )
                    }
                } ?: emptyList()

                val newTopThree = allSeasonalAnime.sortedByDescending { it.rating }.take(3)
                val newTopSeasonal = allSeasonalAnime.take(10)

                val newTopAiring = airingResponse.data?.Page?.media?.mapNotNull { media ->
                    media?.let {
                        Anime(
                            id = it.id,
                            title = it.title?.english ?: it.title?.native ?: "Unknown",
                            imageUrl = it.bannerImage ?: "",
                            rating = it.averageScore ?: 0,
                            description = it.description ?: "",
                            season = it.season ?: MediaSeason.UNKNOWN__,
                            genres = it.genres?.mapNotNull { genre -> genre } ?: emptyList(),
                            coverImage = it.coverImage?.large ?: "",
                            seasonYear = it.seasonYear ?: 1500
                        )
                    }
                }?.take(10) ?: emptyList()

                MemCache.updateTopSeasonal(newTopSeasonal)
                MemCache.updateTopAiring(newTopAiring)
                isLoading.value = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                //.verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Welcome to Aiko",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                VersionInfoRow(
                    updateAvailable = versionCheck ?: AppVersion.VersionCheckResult.UpToDate,
                    onUpdateClick = {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(URI("https://github.com/dubskysteam/aiko/releases"))
                        } else {
                            Logger.log(
                                LogLevel.ERROR,
                                "HomeScreen",
                                "Opening GitHub link is not supported on this platform."
                            )
                        }
                    },
                    onPatchNotesClick = {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(URI("https://github.com/dubskysteam/aiko/releases"))
                        } else {
                            Logger.log(
                                LogLevel.ERROR,
                                "HomeScreen",
                                "Opening GitHub link is not supported on this platform."
                            )
                        }
                    }
                )
            }

            if (isLoading.value) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onBackground,
                        strokeWidth = 6.dp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    TopThreePreview(MemCache.topSeasonalAnime.take(3), onAnimeSelected)

                    AnimeSection("Romance", onAnimeSelected)

                    Spacer(modifier = Modifier.height(600.dp))

                    AnimeSection("Action", onAnimeSelected)
                    AnimeSection("Comedy", onAnimeSelected)
                    AnimeSection("Drama", onAnimeSelected)
                }
            }
        }
    }
}

@Composable
private fun TopThreePreview(
    animeList: List<Anime>,
    onAnimeSelected: (Anime) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val screenWidth = maxWidth
        val cardWidth = screenWidth / 1.1f / 3  // 3 Cards + spacing
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(animeList) { anime ->
                AnimeCard(
                    anime = anime,
                    onClick = { onAnimeSelected(anime) },
                    modifier = Modifier
                        .width(cardWidth)
                        .aspectRatio(1.5f)
                )
            }
        }
    }
}


@Composable
private fun AnimeSection(
    sectionTitle: String,
    onAnimeSelected: (Anime) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "View more",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            userScrollEnabled = false
        ) {
            items(MemCache.topSeasonalAnime.take(10)) { anime ->
                AnimeCard(
                    anime = anime,
                    onClick = { onAnimeSelected(anime) },
                    modifier = Modifier.aspectRatio(0.7f)
                )
            }
        }

    }
}


@Composable
private fun VersionInfoRow(
    updateAvailable: AppVersion.VersionCheckResult,
    onUpdateClick: () -> Unit,
    onPatchNotesClick: () -> Unit
) {
    var showVersionMenu by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box {
            TextButton(
                onClick = { showVersionMenu = true },
                modifier = Modifier.height(36.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = AppVersion.CURRENT_VERSION,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Version menu",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = showVersionMenu,
                onDismissRequest = { showVersionMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Current: ${AppVersion.CURRENT_VERSION}",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    onClick = { showVersionMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("View Patch Notes") },
                    onClick = {
                        onPatchNotesClick()
                        showVersionMenu = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = onUpdateClick,
            enabled = updateAvailable != AppVersion.VersionCheckResult.BetaVersion && updateAvailable != AppVersion.VersionCheckResult.UpToDate,
            modifier = Modifier.height(36.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (updateAvailable is AppVersion.VersionCheckResult.UpdateAvailable) Color.Red
                else MaterialTheme.colorScheme.surface,
                contentColor = if (updateAvailable is AppVersion.VersionCheckResult.UpdateAvailable) MaterialTheme.colorScheme.onTertiary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = when (updateAvailable) {
                    is AppVersion.VersionCheckResult.UpdateAvailable -> "Update"
                    AppVersion.VersionCheckResult.BetaVersion -> "Beta"
                    else -> "Up to date"
                },
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}