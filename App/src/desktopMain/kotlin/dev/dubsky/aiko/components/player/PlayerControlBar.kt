package dev.dubsky.aiko.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uk.co.caprica.vlcj.player.base.MediaPlayer

@Composable
fun PlayerControlBar(
    mediaPlayer: MediaPlayer,
    isFullscreen: Boolean,
    onFullscreenToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { /* Previous episode */ }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Episode", tint = Color.White)
        }
        IconButton(onClick = { mediaPlayer.controls().play() }) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play/Pause", tint = Color.White)
        }
        IconButton(onClick = { /* Next episode */ }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Episode", tint = Color.White)
        }
        IconButton(onClick = onFullscreenToggle) {
            Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen", tint = Color.White)
        }
    }
}
