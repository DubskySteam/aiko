package dev.dubsky.aiko.config

import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

object AppVersion {
    const val CURRENT_VERSION = "1.0.0"

    private const val VERSION_FILE_URL = "https://raw.githubusercontent.com/DubskySteam/aiko/refs/heads/VERSION_CHECK/VERSION"

    sealed class VersionCheckResult {
        data class UpdateAvailable(val current: String, val latest: String) : VersionCheckResult()
        object UpToDate : VersionCheckResult()
        data class Error(val message: String) : VersionCheckResult()
    }

    suspend fun checkForUpdates(): VersionCheckResult {
        return try {
            val latestVersion = withContext(Dispatchers.IO) {
                URL(VERSION_FILE_URL).openStream().use {
                    it.bufferedReader().readLine().trim()
                }
            }

            Logger.log(LogLevel.INFO, "AppVersion", "Latest version: $latestVersion (current: $CURRENT_VERSION)")
            when {
                latestVersion > CURRENT_VERSION ->
                    VersionCheckResult.UpdateAvailable(CURRENT_VERSION, latestVersion)
                else ->
                    VersionCheckResult.UpToDate
            }
        } catch (e: Exception) {
            VersionCheckResult.Error(e.message ?: "Unknown error")
        }
    }
}