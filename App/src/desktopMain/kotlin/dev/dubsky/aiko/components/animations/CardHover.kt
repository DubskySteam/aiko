package dev.dubsky.aiko.components.animations

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.responsiveHover(): Modifier = composed {
    var hovered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (hovered) 1.04f else 1f)
    this
        .onPointerEvent(PointerEventType.Enter) {
            hovered = true
        }
        .onPointerEvent(PointerEventType.Exit) {
            hovered = false
        }
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            shadowElevation = if (hovered) 12f else 4f
        }
}