package dev.dubsky.aiko.screens

import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.data.Anime
import dev.dubsky.aiko.graphql.type.MediaSeason

@Composable
fun StarRating(rating: Int) {
    Row {
        repeat(rating/10) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                modifier = Modifier.size(24.dp),
                tint = if (index < rating) Color.Yellow else Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeTags(tags: List<String>) {
    FlowRow(
        modifier = Modifier
            .padding(32.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            Chip(
                text = tag,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun Chip(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.primary.copy(alpha = 0.2f))
            .border(1.dp, MaterialTheme.colors.primary, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colors.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
@Composable
fun AnimeBanner(imgUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        AsyncImage(
            model = imgUrl,
            contentDescription = "Anime Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun AnimeLogo(imgUrl: String) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(300.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, Color.White, RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = imgUrl,
            contentDescription = "Anime Logo",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun AnimeInfo(
    title: String,
    rating: Int,
    description: String,
    season: MediaSeason,
    year: Int
) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h4,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StarRating(rating)
            Text(
                text = "$season $year",
                color = Color(0xFFADB5BD),
                style = MaterialTheme.typography.subtitle1
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = description,
            color = Color.White,
            style = MaterialTheme.typography.body1,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun AnimeScreen(anime: Anime, onPlayerClick: () -> Unit) {
    var showConfigError by remember { mutableStateOf(false) }

    if(showConfigError) {
        AlertDialog(
            onDismissRequest = { showConfigError = false },
            title = { Text("Configuration Error") },
            text = { Text("Please check your API/Proxy/Refer configuration in settings.\nAll fields must be filled out!") },
            confirmButton = {
                Button(onClick = { showConfigError = false }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1A1A), Color(0xFF2D2D2D))
                )
            )
    ) {
        AnimeBanner(imgUrl = anime.imageUrl)

        Row(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(300.dp)
                    .shadow(16.dp, RoundedCornerShape(12.dp))
            ) {
                AnimeLogo(imgUrl = anime.coverImage)
            }

            Spacer(modifier = Modifier.width(32.dp))

            Column(modifier = Modifier.weight(1f)) {
                AnimeInfo(
                    title = anime.title,
                    rating = anime.rating,
                    description = anime.description,
                    season = anime.season,
                    year = anime.seasonYear
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if(ConfigManager.isValid()) {
                            onPlayerClick()
                        } else {
                            showConfigError = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    //colors = ButtonDefaults.buttonColors(
                    //    backgroundColor = MaterialTheme.colors.primary,
                    //    contentColor = Color.White
                    //)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Watch",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Watch Now", fontSize = 16.sp)
                }
            }
        }

        AnimeTags(tags = anime.genres)
    }
}