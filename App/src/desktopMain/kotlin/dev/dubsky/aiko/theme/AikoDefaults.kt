package dev.dubsky.aiko.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

object AikoDefaults {

    val segmentedButtonColors: SegmentedButtonColors
        @Composable get() = SegmentedButtonColors(
            activeContainerColor = MaterialTheme.colorScheme.primary,
            activeContentColor = MaterialTheme.colorScheme.onPrimary,
            activeBorderColor = MaterialTheme.colorScheme.primary,
            inactiveContainerColor = MaterialTheme.colorScheme.background,
            inactiveContentColor = MaterialTheme.colorScheme.onSurface,
            inactiveBorderColor = MaterialTheme.colorScheme.background,
            disabledActiveContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledActiveContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledActiveBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledInactiveContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledInactiveContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledInactiveBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )


    val cardColors: CardColors
        @Composable get() = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )

    val cardElevation: CardElevation
        @Composable get() = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            focusedElevation = 6.dp,
            hoveredElevation = 6.dp
        )

}