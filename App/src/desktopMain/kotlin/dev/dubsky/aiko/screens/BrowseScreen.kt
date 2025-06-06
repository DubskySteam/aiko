package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import dev.dubsky.aiko.api.AnimeData
import dev.dubsky.aiko.components.animations.responsiveHover
import dev.dubsky.aiko.components.card.AnimeCard
import dev.dubsky.aiko.config.ConfigManager
import dev.dubsky.aiko.data.Anime
import dev.dubsky.aiko.graphql.type.MediaSeason
import dev.dubsky.aiko.graphql.type.MediaStatus
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    onAnimeSelected: (Anime) -> Unit = {},
    windowSize: DpSize
) {
    var displayedAnime by remember { mutableStateOf<List<Anime>>(emptyList()) }
    var rawAnime by remember { mutableStateOf<List<Anime>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var filters by remember {
        mutableStateOf(
            AnimeFilters(
                searchQuery = "",
                season = MediaSeason.UNKNOWN__,
                year = null,
                genres = emptyList(),
                minRating = 0f,
                status = MediaStatus.UNKNOWN__,
                resultSize = 20
            )
        )
    }

    val columns by remember(windowSize.width) {
        derivedStateOf {
            when {
                windowSize.width < 600.dp -> 2
                windowSize.width < 900.dp -> 4
                windowSize.width < 1200.dp -> 6
                windowSize.width < 1800.dp -> 8
                else -> 10
            }
        }
    }

    LaunchedEffect(searchQuery, rawAnime) {
        displayedAnime = if (searchQuery.isEmpty() || searchQuery == "") {
            rawAnime
        } else {
            rawAnime.filter {
                it.title.contains(searchQuery, ignoreCase = true)
            }
        }

    }

    fun loadAnimeData() {
        isLoading = true
        coroutineScope.launch {
            try {
                val response = AnimeData().getByFilter(
                    season = filters.season,
                    seasonYear = filters.year,
                    status = filters.status,
                    genre = filters.genres.map { it.displayName },
                    averageScore_greater = (filters.minRating * 10).toInt(),
                    perPage = filters.resultSize,
                    search = if (searchQuery != "" || searchQuery.isEmpty()) searchQuery else null,
                    isAdult = ConfigManager.config.adult
                )

                rawAnime = response.data?.Page?.media?.mapNotNull { media ->
                    media?.let {
                        Anime(
                            id = it.id,
                            title = it.title?.english ?: it.title?.native ?: "Unknown",
                            coverImage = it.coverImage?.large ?: "",
                            rating = it.averageScore ?: 0,
                            season = it.season ?: MediaSeason.UNKNOWN__,
                            seasonYear = it.seasonYear ?: 0,
                            genres = it.genres?.mapNotNull { genre -> genre } ?: emptyList(),
                            imageUrl = it.bannerImage ?: "",
                            description = it.description ?: ""
                        )
                    }
                } ?: emptyList()

                searchQuery = ""
            } catch (e: Exception) {
                Logger.log(LogLevel.ERROR, "BrowseScreen", "Failed to load anime: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(2f),
                placeholder = { Text("Search anime...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )

            FilterDropdown(
                label = filters.season.takeIf { it != MediaSeason.UNKNOWN__ }?.name?.capitalize() ?: "Season",
                options = listOf(MediaSeason.UNKNOWN__) + MediaSeason.entries.filter { it != MediaSeason.UNKNOWN__ },
                onOptionSelected = { filters = filters.copy(season = it) },
                modifier = Modifier.weight(1f),
                optionToString = { if (it == MediaSeason.UNKNOWN__) "Any" else it.name.capitalize() }
            )

            FilterDropdown(
                label = filters.year?.toString() ?: "Year",
                options = listOf<Int?>(null) + (1980..2025).toList().reversed(),
                onOptionSelected = { filters = filters.copy(year = it) },
                modifier = Modifier.weight(1f),
                optionToString = { it?.toString() ?: "Any" }
            )

            FilterDropdown(
                label = filters.status.takeIf { it != MediaStatus.UNKNOWN__ }?.name?.capitalize() ?: "Status",
                options = listOf(MediaStatus.UNKNOWN__) + MediaStatus.entries.filter { it != MediaStatus.UNKNOWN__ },
                onOptionSelected = { filters = filters.copy(status = it) },
                modifier = Modifier.weight(1f),
                optionToString = { if (it == MediaStatus.UNKNOWN__) "Any" else it.name.capitalize() }
            )

            var genreExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = when {
                        filters.genres.isEmpty() -> "Genres"
                        filters.genres.size == 1 -> filters.genres.first().displayName
                        else -> "${filters.genres.size} selected"
                    },
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            null,
                            modifier = Modifier.clickable { genreExpanded = true }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { genreExpanded = true },
                    shape = RoundedCornerShape(12.dp)
                )

                DropdownMenu(
                    expanded = genreExpanded,
                    onDismissRequest = { genreExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    AnimeGenre.entries.forEach { genre ->
                        DropdownMenuItem(
                            onClick = {
                                filters = filters.copy(
                                    genres = if (filters.genres.contains(genre)) {
                                        filters.genres - genre
                                    } else {
                                        filters.genres + genre
                                    }
                                )
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = filters.genres.contains(genre),
                                        onCheckedChange = null
                                    )
                                    Text(genre.displayName)
                                }
                            }
                        )
                    }
                }
            }

            val ratingOptions = remember {
                (0 until 10).map { it }
            }
            FilterDropdown(
                label = if (filters.minRating > 0) "${filters.minRating}+" else "Rating",
                options = ratingOptions,
                onOptionSelected = { filters = filters.copy(minRating = it.toFloat()) },
                modifier = Modifier.weight(1f)
            )

            FilterDropdown(
                label = filters.resultSize.toString(),
                options = listOf(20, 40, 60),
                onOptionSelected = { filters = filters.copy(resultSize = it) },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { loadAnimeData() },
                modifier = Modifier.height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Search")
            }
        }

        Text(
            text = "${displayedAnime.size} results",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displayedAnime) { anime ->
                    AnimeCard(
                        anime = anime,
                        onClick = { onAnimeSelected(anime) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.7f)
                            .responsiveHover()
                    )
                }
            }
        }
    }
}


@Composable
private fun <T> FilterDropdown(
    label: String,
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    optionToString: (T) -> String = { it.toString() }
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        ) {
            OutlinedTextField(
                value = label,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        null,
                        modifier = Modifier.clickable { expanded = true }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .heightIn(max = 400.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionToString(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun RatingBar(rating: Float) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = when {
                    rating >= index + 1 -> Icons.Filled.Star
                    rating > index -> Icons.AutoMirrored.Filled.StarHalf
                    else -> Icons.Filled.StarOutline
                },
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

data class AnimeFilters(
    val searchQuery: String = "",
    val season: MediaSeason = MediaSeason.UNKNOWN__,
    val year: Int? = null,
    val genres: List<AnimeGenre> = emptyList(),
    val minRating: Float = 0f,
    val status: MediaStatus = MediaStatus.UNKNOWN__,
    val resultSize: Int = 20
)

enum class AnimeGenre(val displayName: String) {
    ACTION("Action"),
    ADVENTURE("Adventure"),
    COMEDY("Comedy"),
    DRAMA("Drama"),
    FANTASY("Fantasy"),
    HISTORICAL("Historical"),
    HORROR("Horror"),
    MAGICAL("Magical"),
    MYSTERY("Mystery"),
    ROMANCE("Romance"),
    SCI_FI("Sci-Fi"),
    THRILLER("Thriller"),
    TRAGIC("Tragic"),
}

fun String.capitalize(): String = this.lowercase().replaceFirstChar { it.uppercase() }