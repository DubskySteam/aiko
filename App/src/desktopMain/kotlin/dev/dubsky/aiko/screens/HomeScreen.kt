package dev.dubsky.aiko.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.dubsky.aiko.api.AnimeData
import dev.dubsky.aiko.api.MemCache
import dev.dubsky.aiko.api.MemCache.topAiringAnime
import dev.dubsky.aiko.api.MemCache.topSeasonalAnime
import dev.dubsky.aiko.components.card.AnimeCard
import dev.dubsky.aiko.config.AppVersion
import dev.dubsky.aiko.data.Anime
import dev.dubsky.aiko.graphql.type.MediaSeason
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import dev.dubsky.aiko.resources.Res
import dev.dubsky.aiko.resources.discord
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import java.awt.Desktop
import java.net.URI

@Composable
fun HomeScreen(
    onAnimeSelected: (Anime) -> Unit = {},
    onBrowseClick: () -> Unit = {},
) {
    var versionCheck by remember { mutableStateOf<AppVersion.VersionCheckResult?>(null) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
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
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
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

@Composable
private fun AnimeGridSection(
    title: String,
    animeList: List<Anime>,
    onAnimeSelected: (Anime) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = Modifier.height(300.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(animeList) { anime ->
                AnimeCard(
                    anime = anime,
                    onClick = { onAnimeSelected(anime) }
                )
            }
        }
    }
}