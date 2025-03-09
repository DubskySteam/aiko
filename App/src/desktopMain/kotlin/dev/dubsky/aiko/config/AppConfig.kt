package dev.dubsky.aiko.config

import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    var Mode: String = "FHD",
    var Logging: Boolean = false,
    var token: String = ""
)