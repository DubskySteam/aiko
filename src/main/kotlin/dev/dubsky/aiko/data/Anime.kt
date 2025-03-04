package dev.dubsky.aiko.data

import dev.dubsky.aiko.graphql.type.MediaSeason

data class Anime(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val rating: Int,
    val description: String,
    val season: MediaSeason,
    val genres: List<String>,
    val coverImage: String,
    val seasonYear: Int
)