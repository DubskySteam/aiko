package dev.dubsky.aiko.config

import dev.dubsky.aiko.theme.AppTheme

data class AppConfig(
    var resolution: String = "FHD",
    var logging: Boolean = false,
    var theme: AppTheme = AppTheme.ORANGE,
    var autoUpdate: Boolean = false,
    var proxy: String = "",
    var refer: String = "",
    var api: String = "",
    var authToken: String = "",
    var userName: String = ""
)