package dev.dubsky.aiko.components.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.awt.Component
import java.util.*

@Composable
fun EmbeddedPlayer(
    mediaPlayer: MediaPlayer,
    mediaPlayerComponent: Component,
    url: String,
    modifier: Modifier,
    isFullscreen: Boolean,
    onFullscreenToggle: () -> Unit
) {
    val factory = remember { { mediaPlayerComponent } }

    DisposableEffect(Unit) { onDispose(mediaPlayer::release) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(16f / 9f)
    ) {
        SwingPanel(
            factory = { mediaPlayerComponent },
            background = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        )
        PlayerControlBar(
            mediaPlayer = mediaPlayer,
            isFullscreen = isFullscreen,
            onFullscreenToggle = onFullscreenToggle,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(8.dp)
        )

    }
}

fun initializeMediaPlayerComponent(): Component {
    NativeDiscovery().discover()
    return if (isMacOS()) {
        CallbackMediaPlayerComponent()
    } else {
        print("Using EmbeddedMediaPlayerComponent")
        EmbeddedMediaPlayerComponent()
    }
}

fun Component.mediaPlayer() = when (this) {
    is CallbackMediaPlayerComponent -> mediaPlayer()
    is EmbeddedMediaPlayerComponent -> mediaPlayer()
    else -> error("mediaPlayer() can only be called on vlcj player components")
}

fun isMacOS(): Boolean {
    val os = System
        .getProperty("os.name", "generic")
        .lowercase(Locale.ENGLISH)
    return "mac" in os || "darwin" in os
}
