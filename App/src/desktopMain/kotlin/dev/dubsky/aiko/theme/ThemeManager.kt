package dev.dubsky.aiko.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color

enum class AppTheme {
    LIGHT, DARK, CUSTOM, ORANGE
}

object ThemeManager {
    var currentTheme: MutableState<AppTheme>? = null
}

private val LightColors = lightColors(
    primary = Color(0xFF6200EE),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6)
)

private val DarkColors = darkColors(
    primary = Color(0xFFBB86FC),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val CustomColors = darkColors(
    primary = Color(0xFFFF9800),
    primaryVariant = Color(0xFFE65100),
    secondary = Color(0xFF00BCD4),
    background = Color(0xFF2E2E2E),
    surface = Color(0xFF424242),
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val DarkOrange = darkColors(
    primary = Color(0xFFFF9800),
    primaryVariant = Color(0xFFE65100),
    secondary = Color(0xff1e1c1c),
    background = Color(0xFF11151C),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun AppThemedContent(content: @Composable () -> Unit) {
    val theme = ThemeManager.currentTheme?.value ?: AppTheme.LIGHT
    val colors = when (theme) {
        AppTheme.LIGHT -> LightColors
        AppTheme.DARK -> DarkColors
        AppTheme.CUSTOM -> CustomColors
        AppTheme.ORANGE -> DarkOrange
    }

    MaterialTheme(colors = colors) {
        content()
    }
}
