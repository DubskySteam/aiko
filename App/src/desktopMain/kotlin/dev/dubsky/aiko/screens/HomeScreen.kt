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
                    updateAvailable = versionCheck is AppVersion.VersionCheckResult.UpdateAvailable,
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

            OutlinedButton(
                onClick = {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(URI("https://discord.gg/KdesEpJMqj"))
                    } else {
                        Logger.log(
                            LogLevel.ERROR,
                            "HomeScreen",
                            "Opening Discord link is not supported on this platform."
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                border = BorderStroke(1.dp, Color(0xFF7289DA)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF7289DA)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.discord),
                        contentDescription = "Discord",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Join Our Discord",
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilledTonalButton(
                onClick = onBrowseClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Browse Anime",
                    style = MaterialTheme.typography.labelLarge,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            AnimeGridSection(
                title = "Top Airing",
                animeList = topAiringAnime,
                onAnimeSelected = onAnimeSelected
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimeGridSection(
                title = "Top Seasonal",
                animeList = topSeasonalAnime,
                onAnimeSelected = onAnimeSelected
            )
        }
    }
}

@Composable
private fun VersionInfoRow(
    updateAvailable: Boolean,
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
            enabled = updateAvailable,
            modifier = Modifier.height(36.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (updateAvailable) Color.Red
                    else MaterialTheme.colorScheme.surface,
                contentColor = if (updateAvailable) MaterialTheme.colorScheme.onTertiary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = if (updateAvailable) "Update" else "Updated",
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

@Composable
fun AnimeCard(
    anime: Anime,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.67f),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = anime.coverImage,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
                    .padding(8.dp)
            ) {
                Text(
                    text = anime.title,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${anime.rating}%",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}