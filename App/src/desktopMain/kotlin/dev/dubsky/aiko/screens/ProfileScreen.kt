package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.dubsky.aiko.api.AnimeData
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import kotlinx.coroutines.launch

data class Profile(
    val id: Long?,
    val username: String,
    val avatar: String,
    val banner: String?,
    val stats: UserStats?
)

data class UserStats(
    val anime: AnimeStats?,
    val manga: MangaStats?
)

data class AnimeStats(
    val count: Int,
    val meanScore: Double,
    val minutesWatched: Int,
    val episodesWatched: Int
)

data class MangaStats(
    val count: Int,
    val meanScore: Double,
    val chaptersRead: Int
)

@Composable
fun ProfileScreen() {
    //var profileData by remember { mutableStateOf<Profile?>(null) }
    //val coroutineScope = rememberCoroutineScope()

    //LaunchedEffect(Unit) {
    //    coroutineScope.launch {
    //        try {
    //            Logger.log(LogLevel.INFO, "Profile", "Attempting to call UserInfo endpoint")
    //            val res = AnimeData().GetUserInfo()
    //            res.data?.Viewer?.let { viewer ->
    //                profileData = Profile(
    //                    id = viewer.id.toLong(),
    //                    username = viewer.name,
    //                    avatar = viewer.avatar?.large ?: "",
    //                    banner = viewer.bannerImage,
    //                    stats = UserStats(
    //                        anime = viewer.statistics?.anime?.let { anime ->
    //                            AnimeStats(
    //                                count = anime.count,
    //                                meanScore = anime.meanScore,
    //                                minutesWatched = anime.minutesWatched,
    //                                episodesWatched = anime.episodesWatched
    //                            )
    //                        },
    //                        manga = viewer.statistics?.manga?.let { manga ->
    //                            MangaStats(
    //                                count = manga.count,
    //                                meanScore = manga.meanScore,
    //                                chaptersRead = manga.chaptersRead
    //                            )
    //                        }
    //                    )
    //                )
    //            }
    //            if (ConfigManager.config.userName != res.data?.Viewer?.name) {
    //                res.data?.Viewer?.name?.let { ConfigManager.setUser(it) }
    //            }
    //        } finally {
    //            Logger.log(LogLevel.INFO, "Profile", "Fetch done")
    //        }
    //    }
    //}

    //Box(
    //    modifier = Modifier
    //        .fillMaxSize()
    //        .padding(32.dp)
    //) {
    //    if (profileData != null) {
    //        Column(
    //            modifier = Modifier
    //                .fillMaxSize()
    //                .verticalScroll(rememberScrollState())
    //        ) {
    //            AsyncImage(
    //                model = profileData?.banner,
    //                contentDescription = "Profile Banner",
    //                contentScale = ContentScale.Crop,
    //                modifier = Modifier
    //                    .fillMaxWidth()
    //                    .height(200.dp)
    //            )

    //            Column(
    //                modifier = Modifier
    //                    .fillMaxWidth()
    //                    .padding(16.dp),
    //                horizontalAlignment = Alignment.CenterHorizontally
    //            ) {
    //                AsyncImage(
    //                    model = profileData?.avatar,
    //                    contentDescription = "Profile Avatar",
    //                    contentScale = ContentScale.Crop,
    //                    modifier = Modifier
    //                        .size(120.dp)
    //                        .clip(CircleShape)
    //                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
    //                )

    //                Spacer(modifier = Modifier.height(16.dp))

    //                Text(
    //                    text = profileData?.username ?: "Unknown",
    //                    fontSize = 24.sp,
    //                    fontWeight = FontWeight.Bold,
    //                    color = MaterialTheme.colorScheme.onBackground
    //                )

    //                Spacer(modifier = Modifier.height(24.dp))

    //                profileData?.stats?.let { stats ->
    //                    StatsSection(stats)
    //                }
    //            }
    //        }
    //    } else {
    //        Box(
    //            modifier = Modifier.fillMaxSize(),
    //            contentAlignment = Alignment.Center
    //        ) {
    //            CircularProgressIndicator()
    //        }
    //    }
    //}


    // CENTERED INFO BOX THAT SAYS "COMING IN NEXT MAJOR UPDATE"
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Coming in next major update",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "This feature is currently in development and will be released in the next major update.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )
        }
    }
}


@Composable
fun StatItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun StatsSection(stats: UserStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Anime Stats",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        stats.anime?.let { anime ->
            StatItem("Anime Count", anime.count.toString())
            StatItem("Mean Score", anime.meanScore.toString())
            StatItem("Minutes Watched", anime.minutesWatched.toString())
            StatItem("Episodes Watched", anime.episodesWatched.toString())
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Manga Stats",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        stats.manga?.let { manga ->
            StatItem("Manga Count", manga.count.toString())
            StatItem("Mean Score", manga.meanScore.toString())
            StatItem("Chapters Read", manga.chaptersRead.toString())
        }
    }
}