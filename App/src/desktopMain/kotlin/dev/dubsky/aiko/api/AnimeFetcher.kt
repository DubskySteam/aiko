package dev.dubsky.aiko.api

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Params {
    const val BASE_API_URL = ""
}

class AnimeFetcher {
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun searchAnime(query: String, page: Int): Result<SearchResult> = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString())
        val url = "${Params.BASE_API_URL}/api/v2/hianime/search?q=$encodedQuery&page=$page"
        executeGetRequest(url).mapCatching { gson.fromJson(it, SearchResult::class.java) }
    }

    suspend fun getEpisodeList(animeId: String): Result<EpisodeList> = withContext(Dispatchers.IO) {
        val url = "${Params.BASE_API_URL}/api/v2/hianime/anime/$animeId/episodes"
        executeGetRequest(url).mapCatching { gson.fromJson(it, EpisodeList::class.java) }
    }

    suspend fun getStreamInfo(episodeId: String): Result<StreamInfo> = withContext(Dispatchers.IO) {
        val url = "${Params.BASE_API_URL}/api/v2/hianime/episode/sources?animeEpisodeId=$episodeId"
        executeGetRequest(url).mapCatching { gson.fromJson(it, StreamInfo::class.java) }
    }

    private suspend fun executeGetRequest(url: String): Result<String> = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code ${response.code}")
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