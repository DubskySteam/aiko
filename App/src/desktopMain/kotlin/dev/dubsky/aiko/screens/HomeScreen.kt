package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.dubsky.aiko.api.AnimeData
import dev.dubsky.aiko.api.MemCache
import dev.dubsky.aiko.components.animations.responsiveHover
import dev.dubsky.aiko.components.card.AnimeCard
import dev.dubsky.aiko.config.AppVersion
import dev.dubsky.aiko.data.Anime
import dev.dubsky.aiko.graphql.type.MediaSeason
import kotlinx.coroutines.launch

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
                val seasonalResponse = AnimeData().getTopAiringAnimeBySeason(pagePer = 50)

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

                MemCache.updateTopSeasonal(allSeasonalAnime)
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
            modifier = Modifier.fillMaxSize()
        ) {
            if (isLoading.value) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
                    PreviewRow(
                        animeList = MemCache.topSeasonalAnime.take(4),
                        onAnimeSelected = onAnimeSelected,
                        windowSize = windowSize
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    CategoryRows(
                        categories = listOf(
                            "Romance" to MemCache.topSeasonalAnime.filter { it.genres.contains("Romance") },
                            "Action" to MemCache.topSeasonalAnime.filter { it.genres.contains("Action") },
                            "Comedy" to MemCache.topSeasonalAnime.filter { it.genres.contains("Comedy") },
                            "Drama" to MemCache.topSeasonalAnime.filter { it.genres.contains("Drama") }
                        ),
                        onAnimeSelected = onAnimeSelected,
                        windowSize = windowSize
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewRow(
    animeList: List<Anime>,
    onAnimeSelected: (Anime) -> Unit,
    windowSize: DpSize
) {
    val spacing = 16.dp
    val cardWidth = ((windowSize.width - spacing * 5) / 4).coerceAtLeast(180.dp)
    val cardHeight = (cardWidth / 16f * 9f).coerceAtLeast(100.dp)
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        items(animeList) { anime ->
            AnimeCard(
                anime = anime,
                onClick = { onAnimeSelected(anime) },
                modifier = Modifier
                    .width(cardWidth)
                    .height(cardHeight)
                    .responsiveHover()
            )
        }
    }
}

@Composable
private fun CategoryRows(
    categories: List<Pair<String, List<Anime>>>,
    onAnimeSelected: (Anime) -> Unit,
    windowSize: DpSize
) {
    val spacing = 24.dp
    val cardMinWidth = 150.dp
    val maxCardsPerCategory = ((windowSize.width / 2 - spacing * 2) / cardMinWidth).toInt().coerceAtLeast(1)
    for (row in 0 until 2) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing / 2),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            for (col in 0 until 2) {
                val catIndex = row * 2 + col
                if (catIndex < categories.size) {
                    val (title, animeList) = categories[catIndex]
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(animeList.take(maxCardsPerCategory)) { anime ->
                                AnimeCard(
                                    anime = anime,
                                    onClick = { onAnimeSelected(anime) },
                                    modifier = Modifier
                                        .width(cardMinWidth)
                                        .aspectRatio(0.7f)
                                        .responsiveHover()
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}