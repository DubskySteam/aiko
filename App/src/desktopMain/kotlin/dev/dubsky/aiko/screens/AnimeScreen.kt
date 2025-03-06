package dev.dubsky.aiko.screens

import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.dubsky.aiko.data.Anime
import dev.dubsky.aiko.graphql.type.MediaSeason

@Composable
fun StarRating(rating: Int) {
    Row {
        repeat(rating/10) { index ->
            Text(
                text = if (index < rating) "★" else "☆",
                fontSize = 24.sp,
                color = if (index < rating) Color.Yellow else Color.Gray
            )
        }
    }
}

@Composable
fun AnimeTags(tags: List<String>) {
    Row(
        modifier = Modifier.padding(top = 8.dp)
    ) {
        tags.forEach { tag ->
            Text(
                text = tag,
                modifier = Modifier
                    .padding(
                        end = 8.dp,
                    )
                    .border(1.dp, Color.Gray, MaterialTheme.shapes.small)
                    .padding(4.dp),
                color = Color.Gray
            )
        }
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
fun AnimeScreen(anime: Anime) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        AnimeBanner(imgUrl = anime.imageUrl)
        Row(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
        ) {
            AnimeLogo(imgUrl = anime.coverImage)
            AnimeInfo(
                title = anime.title,
                rating = anime.rating,
                description = anime.description,
                season = anime.season,
                year = anime.seasonYear
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.padding(start = 32.dp) ) {
            AnimeTags(tags = anime.genres)
        }
    }
}

@Preview
@Composable
fun PreviewBanner() {
    AnimeBanner(imgUrl = "https://s4.anilist.co/file/anilistcdn/media/anime/banner/176496-5oY4k2NRqlYs.jpg")
}

@Preview
@Composable
fun PreviewLogo() {
    AnimeLogo(imgUrl = "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx110960-4Z8Z3ZQ8Z6Zz.jpg")
}

@Preview
@Composable
fun PreviewAnimeInfo() {
    AnimeInfo(
        title = "Solo Leveling",
        rating = 85,
        description = "In this world where Hunters with various magical powers battle monsters from invading the defenceless humanity, Sung Jin-Woo was the weakest of all the Hunters...",
        season = MediaSeason.WINTER,
        year = 2025
    )
}

@Preview
@Composable
fun PreviewTags() {
    AnimeTags(tags = listOf("Action", "Adventure", "Fantasy"))
}

@Preview
@Composable
fun PreviewAnimeScreen() {
    AnimeScreen(
        anime = Anime(
            id = 1,
            title = "Solo Leveling",
            imageUrl = "https://s4.anilist.co/file/anilistcdn/media/anime/banner/176496-5oY4k2NRqlYs.jpg",
            rating = 85,
            description = "In this world where Hunters with various magical powers battle monsters from invading the defenceless humanity, Sung Jin-Woo was the weakest of all the Hunters, barely able to make a living. However, a mysterious System grants him the power of the 'Player', setting him on a course for an incredible and often times perilous Journey.",
            season = MediaSeason.WINTER,
            genres = listOf("Action", "Adventure", "Fantasy"),
            coverImage = "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx110960-4Z8Z3ZQ8Z6Zz.jpg",
            seasonYear = 2025
        )
    )
}