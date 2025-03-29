package dev.dubsky.aiko.config

import dev.dubsky.aiko.theme.AppTheme
import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    var Mode: String = "FHD",
    var Logging: Boolean = false,
    var Theme: AppTheme = AppTheme.ORANGE,
    var Proxy: String = "",
    var token: String = "",
    var userName: String = ""
)