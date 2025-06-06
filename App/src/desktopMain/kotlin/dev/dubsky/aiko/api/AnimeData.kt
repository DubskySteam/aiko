package dev.dubsky.aiko.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.api.http.HttpRequest
import com.apollographql.apollo.api.http.HttpResponse
import com.apollographql.apollo.network.http.HttpInterceptor
import com.apollographql.apollo.network.http.HttpInterceptorChain
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.graphql.*
import dev.dubsky.aiko.graphql.type.MediaSeason
import dev.dubsky.aiko.graphql.type.MediaStatus
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger

class AuthorizationInterceptor : HttpInterceptor {
    override suspend fun intercept(request: HttpRequest, chain: HttpInterceptorChain): HttpResponse {
        Logger.log(LogLevel.INFO, "API", "Attempting to intercept HTTP Call")
        var token = ConfigManager.config.authToken.substringBefore('&')
        val response = chain.proceed(request.newBuilder().addHeader("Authorization", "Bearer $token").build())
        return if (response.statusCode == 401) {
            chain.proceed(request.newBuilder().addHeader("Authorization", "Bearer $token").build())
        } else {
            response
        }
    }
}

class AnimeData {


    private fun buildAuthApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://graphql.anilist.co")
            .addHttpInterceptor(AuthorizationInterceptor())
            .build()
    }

    private fun buildApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://graphql.anilist.co")
            .build()
    }

    suspend fun getTopAnime(page: Int = 1, pagePer: Int = 10): ApolloResponse<TopAnimeByRatingQuery.Data> {
        val apolloClient = buildApolloClient()
        val response =
            apolloClient.query(TopAnimeByRatingQuery(Optional.present(page), Optional.present(pagePer))).execute()
        return response
    }

    suspend fun getTopAiringAnime(page: Int = 1, pagePer: Int = 10): ApolloResponse<TopAnimeAiringQuery.Data> {
        val apolloClient = buildApolloClient()
        val response =
            apolloClient.query(TopAnimeAiringQuery(Optional.present(page), Optional.present(pagePer))).execute()
        return response
    }

    suspend fun getTopAiringAnimeBySeason(
        page: Int = 1,
        pagePer: Int = 10,
        season: MediaSeason = MediaSeason.SPRING,
        year: Int = 2025
    ): ApolloResponse<TopAnimeBySeasonQuery.Data> {
        val apolloClient = buildApolloClient()
        val response = apolloClient.query(
            TopAnimeBySeasonQuery(
                Optional.present(page),
                Optional.present(pagePer),
                Optional.present(season),
                Optional.present(year)
            )
        ).execute()
        return response
    }

    suspend fun getByFilter(
        page: Int = 1,
        perPage: Int,
        season: MediaSeason,
        seasonYear: Int?,
        status: MediaStatus,
        averageScore_greater: Int,
        search: String?,
        isAdult: Boolean? = false,
        genre: List<String>? = null,
    ): ApolloResponse<GetByFilterQuery.Data> {
        val apolloClient = buildApolloClient()
        val nSeason = if (season == MediaSeason.UNKNOWN__) null else season
        val nStatus = if (status == MediaStatus.UNKNOWN__) null else status
        val nSearch = if (search == "" || search == null) null else search
        val _genre = if (genre == null || genre.isEmpty()) null else genre
        val response = apolloClient.query(
            GetByFilterQuery(
                Optional.present(page),
                Optional.present(perPage),
                Optional.presentIfNotNull(nSeason),
                Optional.presentIfNotNull(seasonYear),
                Optional.presentIfNotNull(nStatus),
                Optional.present(averageScore_greater),
                Optional.presentIfNotNull(nSearch),
                Optional.presentIfNotNull(isAdult),
                Optional.presentIfNotNull(_genre)
            )
        ).execute()
        Logger.log(
            LogLevel.INFO,
            "API",
            "Params: page: $page, perPage: $perPage, season: $season, seasonYear: $seasonYear, status: $status, averageScore_greater: $averageScore_greater, search: $search, isAdult: $isAdult, genre: $genre"
        )
        return response
    }

    suspend fun GetUserInfo(): ApolloResponse<UserInfoQuery.Data> {
        val apolloClient = buildAuthApolloClient()
        val response =
            apolloClient.query(UserInfoQuery()).execute()
        Logger.log(LogLevel.INFO, "API", "UserInfo retrieved")
        return response
    }

    suspend fun GetUserAnimeList(): ApolloResponse<UserInfoExtendedQuery.Data> {
        val apolloClient = buildApolloClient()
        val response = apolloClient.query(UserInfoExtendedQuery(userName = Optional.present("Dubsky"))).execute()
        Logger.log(LogLevel.INFO, "API", "User animes retrieved")
        Logger.log(LogLevel.INFO, "API", "data: ${response.data?.MediaListCollection?.lists?.size}")
        return response
    }

}