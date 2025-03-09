package dev.dubsky.aiko.config

import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    var Mode: String = "FHD",
    val Logging: Boolean = false,
    var token: String = ""
)