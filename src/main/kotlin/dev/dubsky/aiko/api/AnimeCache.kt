package dev.dubsky.aiko.api

import dev.dubsky.aiko.data.Anime
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AnimeCache {
    private const val CACHE_DURATION = 5 * 60 * 1000 // 5 minutes in milliseconds

    private data class CachedData<T>(
        val data: T,
        val timestamp: Long
    )

    private var topAiringCache: CachedData<List<Anime>>? = null
    private var topSeasonalCache: CachedData<List<Anime>>? = null
    private var topThreeCache: CachedData<List<Anime>>? = null

    var topAiringAnime by mutableStateOf<List<Anime>>(emptyList())
        private set
    var topSeasonalAnime by mutableStateOf<List<Anime>>(emptyList())
        private set
    var topThreeSeasonalAnime by mutableStateOf<List<Anime>>(emptyList())
        private set

    private fun isValid(cache: CachedData<*>): Boolean =
        System.currentTimeMillis() - cache.timestamp < CACHE_DURATION

    fun needsRefresh(): Boolean {
        return topAiringCache?.let { !isValid(it) } ?: true ||
               topSeasonalCache?.let { !isValid(it) } ?: true ||
               topThreeCache?.let { !isValid(it) } ?: true
    }

    fun updateTopAiring(data: List<Anime>) {
        topAiringCache = CachedData(data, System.currentTimeMillis())
        topAiringAnime = data
    }

    fun updateTopSeasonal(data: List<Anime>) {
        topSeasonalCache = CachedData(data, System.currentTimeMillis())
        topSeasonalAnime = data
    }

    fun updateTopThree(data: List<Anime>) {
        topThreeCache = CachedData(data, System.currentTimeMillis())
        topThreeSeasonalAnime = data
    }
}