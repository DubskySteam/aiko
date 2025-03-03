package dev.dubsky.aiko.screens

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import dev.dubsky.aiko.data.Anime

@Composable
fun AnimeBanner(imgUrl: String) {
    MaterialTheme {
        Box(
            modifier = Modifier.size(720.dp, 151.dp).border(1.dp, Color.Black, MaterialTheme.shapes.medium)
        ) {
            AsyncImage(
                model = imgUrl,
                contentDescription = "Anime Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun AnimeScreen(anime: Anime) {
    MaterialTheme {
        Column {
            Row {
                AnimeBanner(imgUrl = anime.imageUrl)
            }
            Row(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = anime.title,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAnimeScreen() {
    AnimeScreen(
        anime = Anime(
            id = 1,
            title = "Solo Leveling",
            imageUrl = "https://s4.anilist.co/file/anilistcdn/media/anime/banner/176496-5oY4k2NRqlYs.jpg",
            rating = 85
        )
    )
}