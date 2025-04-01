package dev.dubsky.aiko.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import dev.dubsky.aiko.config.ConfigManager

enum class AppTheme {
    LIGHT, DARK, CUSTOM, ORANGE, PURPLE
}

object ThemeManager {
    var currentTheme: MutableState<AppTheme> = mutableStateOf(ConfigManager.config.theme)
}

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    secondary = Color(0xFF625B71),
    tertiary = Color(0xFF7D5260),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    background = Color(0xFFFFFBFE)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFFCCC2DC),
    tertiary = Color(0xFFEFB8C8),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E1E5),
    background = Color(0xFF121212)
)

private val CustomColorScheme = darkColorScheme(
    primary = Color(0xFFFF9800),
    secondary = Color(0xFF00BCD4),
    tertiary = Color(0xFF03A9F4),
    surface = Color(0xFF424242),
    onSurface = Color.White,
    background = Color(0xFF2E2E2E)
)

private val OrangeColorScheme = darkColorScheme(
    primary = Color(0xFFFF9800),
    secondary = Color(0xFF1E1C1C),
    tertiary = Color(0xFFE65100),
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    background = Color(0xFF11151C)
)

private val PurpleColorScheme = darkColorScheme(
    primary = Color(0xFFA21FFF),
    secondary = Color(0xFF1E1C1C),
    tertiary = Color(0xFF8420FF),
    surface = Color(0xFF1E1E1E),
    background = Color(0xFF11151C),
    onPrimary = Color.White,
    onSurface = Color.White,
    onBackground = Color.White,
)

@Composable
fun AikoTheme(
    theme: AppTheme = ThemeManager.currentTheme.value,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        AppTheme.LIGHT -> LightColorScheme
        AppTheme.DARK -> DarkColorScheme
        AppTheme.CUSTOM -> CustomColorScheme
        AppTheme.ORANGE -> OrangeColorScheme
        AppTheme.PURPLE -> PurpleColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}