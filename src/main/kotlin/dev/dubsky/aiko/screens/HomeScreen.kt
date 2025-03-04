package dev.dubsky.aiko.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import dev.dubsky.aiko.api.AnimeData
import dev.dubsky.aiko.data.Anime
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun AnimeCard(anime: Anime, cardWidth: Dp, cardHeight: Dp, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        backgroundColor = Color(0xFF1E2A38),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight * 0.7f)
                    .background(Color.DarkGray)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    Text(
                        text = anime.title.take(1),
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Text(
                text = anime.title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun HomeScreen(onAnimeSelected: (Anime) -> Unit = {}) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var topAiringAnime by remember { mutableStateOf<List<Anime>>(emptyList()) }
    var topSeasonalAnime by remember { mutableStateOf<List<Anime>>(emptyList()) }
    var topThreeSeasonalAnime by remember { mutableStateOf<List<Anime>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val airingResponse = AnimeData().getTopAiringAnime()
            val seasonalResponse = AnimeData().getTopAiringAnime()

            val allSeasonalAnime = seasonalResponse.data?.Page?.media?.mapNotNull { media ->
                media?.let {
                    Anime(
                        id = it.id,
                        title = it.title?.english ?: it.title?.native ?: "Unknown",
                        imageUrl = it.bannerImage ?: "",
                        rating = it.averageScore ?: 0
                    )
                }
            } ?: emptyList()

            topThreeSeasonalAnime = allSeasonalAnime.sortedByDescending { it.rating }.take(3)
            topSeasonalAnime = allSeasonalAnime.drop(3).take(10)
            topAiringAnime = airingResponse.data?.Page?.media?.mapNotNull { media ->
                media?.let {
                    Anime(
                        id = it.id,
                        title = it.title?.english ?: it.title?.native ?: "Unknown",
                        imageUrl = it.bannerImage ?: "",
                        rating = it.averageScore ?: 0
                    )
                }
            }?.take(10) ?: emptyList()
        }
    }

    MaterialTheme {
        BoxWithConstraints {
            val maxWidth = maxWidth
            val cardPadding = 8.dp
            val cardsPerRow = 5
            val totalPadding = (cardsPerRow + 1) * cardPadding
            val cardWidth = (maxWidth - totalPadding) / (cardsPerRow * 2)  // Reduce width by 50%
            val cardHeight = cardWidth * 1.5f  // Maintain aspect ratio

            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(vertical = 16.dp)
            ) {
                // Top 3 Seasonal Anime
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    topThreeSeasonalAnime.forEach { anime ->
                        AnimeCard(anime = anime, cardWidth = cardWidth * 1.2f, cardHeight = cardHeight * 1.2f, onClick = {
                            onAnimeSelected(anime)
                        })
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Two Column Layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Top Airing",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                        topAiringAnime.chunked(cardsPerRow).forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                row.forEach { AnimeCard(anime = it, cardWidth = cardWidth, cardHeight = cardHeight,
                                    onClick = {
                                        onAnimeSelected(it)
                                    }) }
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Top Seasonal",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                        topSeasonalAnime.chunked(cardsPerRow).forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                row.forEach { AnimeCard(anime = it, cardWidth = cardWidth, cardHeight = cardHeight
                                    , onClick = {
                                        onAnimeSelected(it)
                                    }) }
                            }
                        }
                    }
                }
            }
        }
    }
}