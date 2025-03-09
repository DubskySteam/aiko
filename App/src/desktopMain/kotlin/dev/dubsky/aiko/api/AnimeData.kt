package dev.dubsky.aiko.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional
import dev.dubsky.aiko.graphql.GetByFilterQuery
import dev.dubsky.aiko.graphql.TopAnimeAiringQuery
import dev.dubsky.aiko.graphql.TopAnimeByRatingQuery
import dev.dubsky.aiko.graphql.TopAnimeBySeasonQuery
import dev.dubsky.aiko.graphql.type.MediaSeason
import dev.dubsky.aiko.graphql.type.MediaStatus

class AnimeData {

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
        season: MediaSeason = MediaSeason.WINTER,
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
        genre: String = "",
        averageScore_greater: Int
    ): ApolloResponse<GetByFilterQuery.Data> {
        val apolloClient = buildApolloClient()
        val nSeason = if (season == MediaSeason.UNKNOWN__) null else season
        val nStatus = if (status == MediaStatus.UNKNOWN__) null else status
        val response = apolloClient.query(
            GetByFilterQuery(
                Optional.present(page),
                Optional.present(perPage),
                Optional.presentIfNotNull(nSeason),
                Optional.presentIfNotNull(seasonYear),
                Optional.presentIfNotNull(nStatus),
                Optional.present(averageScore_greater)
            )
        ).execute()
        return response
    }
}