package dev.dubsky.aiko.api

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional
import dev.dubsky.aiko.graphql.TopAnimeAiringQuery
import dev.dubsky.aiko.graphql.TopAnimeByRatingQuery
import dev.dubsky.aiko.graphql.TopAnimeBySeasonQuery
import dev.dubsky.aiko.graphql.type.MediaSeason

class AnimeData {

    private fun buildApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://graphql.anilist.co")
            .build()
    }

    suspend fun getTopAnime(page: Int = 1, pagePer: Int = 10): ApolloResponse<TopAnimeByRatingQuery.Data> {
        val apolloClient = buildApolloClient()
        val response = apolloClient.query(TopAnimeByRatingQuery(Optional.present(page), Optional.present(pagePer))).execute()
        return response
    }

    suspend fun getTopAiringAnime(page: Int = 1, pagePer: Int = 10): ApolloResponse<TopAnimeAiringQuery.Data> {
        val apolloClient = buildApolloClient()
        val response = apolloClient.query(TopAnimeAiringQuery(Optional.present(page), Optional.present(pagePer))).execute()
        return response
    }

    suspend fun getTopAiringAnimeBySeason(page: Int = 1, pagePer: Int = 10, season: MediaSeason = MediaSeason.WINTER, year: Int = 2025): ApolloResponse<TopAnimeBySeasonQuery.Data> {
        val apolloClient = buildApolloClient()
        val response = apolloClient.query(TopAnimeBySeasonQuery(Optional.present(page), Optional.present(pagePer), Optional.present(season), Optional.present(year))).execute()
        return response
    }
}