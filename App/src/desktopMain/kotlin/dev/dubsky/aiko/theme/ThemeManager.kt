package dev.dubsky.aiko.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.resources.*
import dev.dubsky.aiko.resources.Poppins_Light
import dev.dubsky.aiko.resources.Poppins_Regular
import dev.dubsky.aiko.resources.Poppins_Thin
import dev.dubsky.aiko.resources.Res

enum class AppTheme {
    LIGHT, ORANGE, PURPLE
}

object ThemeManager {
    var currentTheme: MutableState<AppTheme> = mutableStateOf(ConfigManager.config.theme)
}

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    secondary = Color(0xFFFFFBFE),
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
    secondary = Color(0xFFFF9800),
    tertiary = Color(0xFFE65100),
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    background = Color(0xFF11151C)
)

private val PurpleColorScheme = darkColorScheme(
    primary = Color(0xFF8420FF),
    secondary = Color(0xFF8420FF),
    tertiary = Color(0xFFA21FFF),
    surface = Color(0xFF1A1E26),
    background = Color(0xFF11151C),
    onPrimary = Color.White,
    onSurface = Color.White,
    onBackground = Color.White,
)

//val poppinsFamily = FontFamily(
//    Font(Res.font.Poppins_Regular.toString(), FontWeight.Normal),
//    Font(Res.font.Poppins_Medium.toString(), FontWeight.Medium),
//    Font(Res.font.Poppins_Bold.toString(), FontWeight.Bold),
//    Font(Res.font.Poppins_Light.toString(), FontWeight.Light),
//    Font(Res.font.Poppins_Thin.toString(), FontWeight.Thin),
//)

//val PoppinsTypography = Typography().copy(
//        bodyLarge = Typography().bodyLarge.copy(fontFamily = poppinsFamily),
//        bodyMedium = Typography().bodyMedium.copy(fontFamily = poppinsFamily),
//        bodySmall = Typography().bodySmall.copy(fontFamily = poppinsFamily),
//        labelLarge = Typography().labelLarge.copy(fontFamily = poppinsFamily),
//        labelMedium = Typography().labelMedium.copy(fontFamily = poppinsFamily),
//        labelSmall = Typography().labelSmall.copy(fontFamily = poppinsFamily),
//        titleLarge = Typography().titleLarge.copy(fontFamily = poppinsFamily),
//        titleMedium = Typography().titleMedium.copy(fontFamily = poppinsFamily),
//        titleSmall = Typography().titleSmall.copy(fontFamily = poppinsFamily),
//        headlineLarge = Typography().headlineLarge.copy(fontFamily = poppinsFamily),
//        headlineMedium = Typography().headlineMedium.copy(fontFamily = poppinsFamily),
//        headlineSmall = Typography().headlineSmall.copy(fontFamily = poppinsFamily),
//        displayLarge = Typography().displayLarge.copy(fontFamily = poppinsFamily),
//        displayMedium = Typography().displayMedium.copy(fontFamily = poppinsFamily),
//        displaySmall = Typography().displaySmall.copy(fontFamily = poppinsFamily),
//    )

fun getColorSchemeByEnum(theme: AppTheme): ColorScheme = when (theme) {
    AppTheme.LIGHT -> LightColorScheme
    AppTheme.ORANGE -> OrangeColorScheme
    AppTheme.PURPLE -> PurpleColorScheme
}

@Composable
fun AikoTheme(
    theme: AppTheme = ThemeManager.currentTheme.value,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        AppTheme.LIGHT -> LightColorScheme
        AppTheme.ORANGE -> OrangeColorScheme
        AppTheme.PURPLE -> PurpleColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        //typography = PoppinsTypography,
        content = content
    )
}