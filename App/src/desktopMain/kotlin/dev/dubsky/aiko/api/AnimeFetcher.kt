package dev.dubsky.aiko.api

import com.google.gson.Gson
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Params {
    var BASE_API_URL = ConfigManager.config.api
}

class HttpException(val code: Int, message: String) : IOException(message)

class AnimeFetcher {
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun searchAnime(query: String, page: Int): Result<SearchResult> = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
        val url = "${Params.BASE_API_URL}/api/v2/hianime/search?q=$encodedQuery&page=$page"
        Logger.log(LogLevel.INFO, "API", "Searching for $query")
        executeGetRequest(url).mapCatching { gson.fromJson(it, SearchResult::class.java) }
    }

    suspend fun getEpisodeList(animeId: String): Result<EpisodeList> = withContext(Dispatchers.IO) {
        val url = "${Params.BASE_API_URL}/api/v2/hianime/anime/$animeId/episodes"
        Logger.log(LogLevel.INFO, "API", "Fetching episode list for $animeId")
        executeGetRequest(url).mapCatching { gson.fromJson(it, EpisodeList::class.java) }
    }

    suspend fun getStreamInfo(episodeId: String): Result<StreamInfo> = withContext(Dispatchers.IO) {
        val servers = listOf("hd-1", "hd-2")
        for (server in servers) {
            val url = "${Params.BASE_API_URL}/api/v2/hianime/episode/sources?animeEpisodeId=$episodeId?server=$server"
            Logger.log(LogLevel.INFO, "API", "Fetching stream info (server: $server) for $episodeId")
            val result = executeGetRequest(url).mapCatching {
                gson.fromJson(it, StreamInfo::class.java)
            }
            when {
                result.isSuccess -> return@withContext result
                else -> {
                    val exception = result.exceptionOrNull()
                    if (exception is HttpException && exception.code == 500) continue
                    else return@withContext result
                }
            }
        }
        Logger.log(LogLevel.ERROR, "API", "Failed to fetch stream info for $episodeId")
        Result.failure(HttpException(500, "Failed after trying all servers"))
    }
    private suspend fun executeGetRequest(url: String): Result<String> = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw HttpException(response.code, "Unexpected code ${response.code}")
                }
                Result.success(response.body?.string() ?: throw IOException("Empty response body"))
            }
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    data class SearchResult(
        val success: Boolean,
        val data: SearchData
    )

    data class SearchData(
        val animes: List<Anime>,
        val mostPopularAnimes: List<PopularAnime>,
        val currentPage: Int,
        val totalPages: Int,
        val hasNextPage: Boolean,
        val searchQuery: String,
        val searchFilters: Map<String, List<String>>
    )

    data class Anime(
        val id: String,
        val name: String,
        val poster: String,
        val duration: String,
        val type: String,
        val rating: String,
        val episodes: Episodes
    )

    data class PopularAnime(
        val episodes: Episodes,
        val id: String,
        val jname: String,
        val name: String,
        val poster: String,
        val type: String
    )

    data class Episodes(
        val sub: Int,
        val dub: Int
    )

    data class EpisodeList(
        val success: Boolean,
        val data: EpisodeData
    )

    data class EpisodeData(
        val totalEpisodes: Int,
        val episodes: List<Episode>
    )

    data class Episode(
        val number: Int,
        val title: String,
        val episodeId: String,
        val isFiller: Boolean
    )

    data class StreamInfo(
        val success: Boolean,
        val data: StreamData
    )

    data class StreamData(
        val tracks: List<Track>,
        val intro: Intro,
        val outro: Outro,
        val sources: List<Source>,
        val anilistID: Int?,
        val malID: Int?
    )

    data class Track(
        val file: String,
        val label: String,
        val kind: String,
        val isDefault: Boolean
    )

    data class Intro(
        val start: Int,
        val end: Int
    )

    data class Outro(
        val start: Int,
        val end: Int
    )

    data class Source(
        val url: String,
        val type: String
    )
}