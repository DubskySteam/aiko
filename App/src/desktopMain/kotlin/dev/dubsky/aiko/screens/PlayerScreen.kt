package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.dubsky.aiko.api.AnimeFetcher
import dev.dubsky.aiko.components.player.EmbeddedPlayer
import dev.dubsky.aiko.components.player.initializeMediaPlayerComponent
import dev.dubsky.aiko.components.player.mediaPlayer
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uk.co.caprica.vlcj.player.base.State
import uk.co.caprica.vlcj.player.base.TrackDescription
import java.awt.Desktop
import java.net.URI
import java.util.*


@Composable
fun PlayerScreen(anime_id: Int, animeTitle: String) {
    var isFullscreen by remember { mutableStateOf(false) }
    val mediaPlayerComponent = remember { initializeMediaPlayerComponent() }
    val mediaPlayer = remember { mediaPlayerComponent.mediaPlayer() }
    var playerStream by remember { mutableStateOf<String?>(null) }
    var animeId by remember { mutableStateOf<String?>(null) }
    var episodes by remember { mutableStateOf<List<AnimeFetcher.Episode>?>(null) }
    var currentEpisode by remember { mutableStateOf<AnimeFetcher.Episode?>(null) }
    var isMuted by remember { mutableStateOf(false) }
    var volume by remember { mutableStateOf(50f) }
    var currentTime by remember { mutableStateOf(0L) }
    var totalTime by remember { mutableStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()

    fun getProxyUrl(videoUrl: String, refererUrl: String): String {
        val combinedUrl = "$videoUrl|$refererUrl"
        val base64EncodedUrl = Base64.getEncoder().encodeToString(combinedUrl.toByteArray())
        Logger.log(LogLevel.INFO, "PlayerScreen", "Base Proxy URL: $videoUrl | $refererUrl")
        Logger.log(LogLevel.INFO, "PlayerScreen", "Encoded Proxy URL: ${ConfigManager.config.Proxy}/$base64EncodedUrl.m3u8")
        return "${ConfigManager.config.Proxy}/$base64EncodedUrl.m3u8"
    }

    fun getSubtitleUrl(streamInfo: AnimeFetcher.StreamInfo): String? {
        return streamInfo.data.tracks
            .firstOrNull { track ->
                track.kind == "captions" && (track.isDefault || track.label == "English")
            }
            ?.file
    }

    LaunchedEffect(animeTitle) {
        coroutineScope.launch {
            try {
                val animeFetcher = AnimeFetcher()
                val searchResult = animeFetcher.searchAnime(animeTitle, 1)
                animeId = searchResult.getOrNull()?.data?.animes?.get(0)?.id
                if (animeId != null) {
                    val episodeList = animeFetcher.getEpisodeList(animeId!!)
                    episodes = episodeList.getOrNull()?.data?.episodes
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(currentEpisode) {
        if (currentEpisode != null) {
            coroutineScope.launch {
                try {
                    val animeFetcher = AnimeFetcher()
                    val streamInfo = animeFetcher.getStreamInfo(currentEpisode!!.episodeId)
                    val directStreamUrl = streamInfo.getOrNull()?.data?.sources?.firstOrNull()?.url
                    if (directStreamUrl != null) {
                        val refererUrl = ConfigManager.config.Refer
                        val proxyStreamUrl = if (ConfigManager.config.Proxy != "") getProxyUrl(directStreamUrl, refererUrl) else directStreamUrl
                        playerStream = proxyStreamUrl
                        Logger.log(LogLevel.INFO, "Player", "Proxy Stream URL loaded")

                        val subtitleUrl = getSubtitleUrl(streamInfo.getOrNull()!!)
                        if (subtitleUrl != null) {
                            Logger.log(LogLevel.INFO, "Player", "Subtitle loaded")
                            if (playerStream != null) {
                                mediaPlayer.media().play(playerStream)
                            }
                            val tracks: List<TrackDescription> = mediaPlayer.subpictures().trackDescriptions();
                            val trackId = tracks.
                                firstOrNull { it.description() != "Disabled"}
                                ?.id()
                            if (trackId != null) {
                                Logger.log(LogLevel.INFO, "Player", "Set track ID to $trackId")
                                mediaPlayer.subpictures().setTrack(trackId.toInt())
                            }
                            mediaPlayer.subpictures().setSubTitleUri(URI(subtitleUrl).toString())
                        } else {
                            Logger.log(LogLevel.INFO, "Player", "Subtitle not found")
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    LaunchedEffect(volume) {
        mediaPlayer.audio().setVolume(volume.toInt())
    }

    LaunchedEffect(isMuted) {
        mediaPlayer.audio().isMute = isMuted
    }

    LaunchedEffect(mediaPlayer) {
        while (true) {
            currentTime = mediaPlayer.status().time()
            totalTime = mediaPlayer.status().length()
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .weight(3f)
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
                    .border(3.dp, MaterialTheme.colors.primary)
            ) {
                EmbeddedPlayer(
                    mediaPlayer = mediaPlayer,
                    mediaPlayerComponent = mediaPlayerComponent,
                    url = playerStream ?: "",
                    modifier = Modifier.matchParentSize().fillMaxSize(),
                    isFullscreen = isFullscreen,
                    onFullscreenToggle = { isFullscreen = !isFullscreen }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                episodes?.let { episodeList ->
                    items(episodeList) { episode ->
                        EpisodeItem(
                            episode = episode,
                            isSelected = episode == currentEpisode,
                            onClick = { currentEpisode = episode }
                        )
                    }
                }
            }
        }

        if (!isFullscreen && totalTime > 0) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Slider(
                    value = currentTime.toFloat(),
                    onValueChange = { newTime ->
                        currentTime = newTime.toLong()
                        mediaPlayer.controls().setTime(currentTime)
                    },
                    valueRange = 0f..totalTime.toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentTime),
                        color = MaterialTheme.colors.onSurface
                    )
                    Text(
                        text = formatTime(totalTime),
                        color = MaterialTheme.colors.onSurface
                    )
                }
            }
        }

        if (!isFullscreen) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = currentEpisode?.let { "${animeTitle.toString()} | Episode ${it.number}: ${it.title}" } ?: "$animeTitle | No Episode Selected",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colors.primary)
            ) {
                IconButton(onClick = {
                    val currentIndex = episodes?.indexOf(currentEpisode) ?: -1
                    if (currentIndex > 0) {
                        currentEpisode = episodes?.get(currentIndex - 1)
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Episode")
                }

                IconButton(onClick = {
                    val currentIndex = episodes?.indexOf(currentEpisode) ?: -1
                    if (currentIndex < (episodes?.size ?: 0) - 1) {
                        currentEpisode = episodes?.get(currentIndex + 1)
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Episode")
                }

                IconButton(onClick = {
                    if (mediaPlayer.media().info().state() == State.PAUSED) {
                        mediaPlayer.controls().play()
                    } else {
                        mediaPlayer.controls().pause()
                    }
                }) {
                    Icon(Icons.Default.PlayCircle, contentDescription = "Play/Pause")
                }

                IconButton(onClick = {
                    Desktop.getDesktop().browse(URI("https://anilist.co/anime/${anime_id}"))
                }) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Open on Anilist")
                }

                IconButton(onClick = { isMuted = !isMuted }) {
                    Icon(
                        if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Mute"
                    )
                }

                Slider(
                    value = volume,
                    onValueChange = { volume = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.width(220.dp)
                    , colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colors.background,
                        inactiveTrackColor = MaterialTheme.colors.background.copy(alpha = 0.5f),
                        thumbColor = MaterialTheme.colors.background
                    )
                )
            }
        }
    }
}

@Composable
fun EpisodeItem(
    episode: AnimeFetcher.Episode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(
                if (isSelected) MaterialTheme.colors.primary.copy(alpha = 0.2f) else Color.Transparent,
                RoundedCornerShape(4.dp)
            )
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Episode ${episode.number}: ${episode.title}",
                color = MaterialTheme.colors.onSurface
            )
            IconButton(onClick = onClick) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play Episode")
            }
        }
    }
}

fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
