package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import dev.dubsky.aiko.api.AnimeData
import dev.dubsky.aiko.data.Anime
import dev.dubsky.aiko.graphql.type.MediaSeason
import dev.dubsky.aiko.graphql.type.MediaStatus
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import kotlinx.coroutines.launch

enum class AnimeGenre(val displayName: String) {
    ACTION("Action"),
    ADVENTURE("Adventure"),
    COMEDY("Comedy"),
    DRAMA("Drama"),
    FANTASY("Fantasy"),
    HORROR("Horror"),
    MYSTERY("Mystery"),
    PSYCHOLOGICAL("Psychological"),
    ROMANCE("Romance"),
    SCI_FI("Sci-Fi"),
    SLICE_OF_LIFE("Slice of Life"),
    SPORTS("Sports"),
    SUPERNATURAL("Supernatural"),
    THRILLER("Thriller");

    companion object {
        fun fromString(value: String): AnimeGenre? = values().find { it.displayName.equals(value, ignoreCase = true) }
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

object AikoTheme {
    val background = Color(0xFF121212)
    val surface = Color(0xFF1E1E1E)
    val primary = Color(0xFF6200EE)
    val secondary = Color(0xFF03DAC6)
    val onBackground = Color.White
    val onSurface = Color.White
    val cardBackground = Color(0xFF1E2A38)
    val inputBackground = Color(0xFF2C2C2C)
    val dropdownBackground = Color(0xFF333333)
}

@ExperimentalMaterialApi
@Composable
fun BrowseScreenAnimeCard(anime: Anime, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        backgroundColor = AikoTheme.cardBackground,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .background(Color.DarkGray)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            ) {
                AsyncImage(
                    model = anime.coverImage,
                    contentDescription = "Anime Cover Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                        .background(AikoTheme.primary.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${anime.rating / 10}",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = anime.title,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CompactSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .background(AikoTheme.inputBackground, RoundedCornerShape(20.dp))
            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 14.sp
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty()) {
                            Text(
                                text = "Search anime...",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
fun CompactDropdown(
    title: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(AikoTheme.inputBackground)
                .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .clickable { onExpandChange(!expanded) }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Expand",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier
                .background(AikoTheme.dropdownBackground)
                .width(IntrinsicSize.Max)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BrowseScreen(onAnimeSelected: (Anime) -> Unit = {}) {
    val coroutineScope = rememberCoroutineScope()

    var filters by remember { mutableStateOf(AnimeFilters()) }
    var animeResults by remember { mutableStateOf<List<Anime>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val resultSizeOptions = listOf(20, 50, 75, 100)
    val years = (2000..2025).toList().reversed()

    var seasonExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    var genreExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var resultSizeExpanded by remember { mutableStateOf(false) }
    var ratingExpanded by remember { mutableStateOf(false) }

    val ratingOptions = listOf(0, 5, 6, 7, 8, 9)

    LaunchedEffect(Unit) {
        isLoading = true
        coroutineScope.launch {
            try {
                val response = AnimeData().getByFilter(
                    season = filters.season,
                    seasonYear = filters.year ?: 2025,
                    status = filters.status,
                    genre = "",
                    averageScore_greater = (filters.minRating * 10).toInt(),
                    perPage = filters.resultSize
                )

                animeResults = response.data?.Page?.media?.mapNotNull { media ->
                    media?.let {
                        Anime(
                            id = it.id,
                            title = it.title?.english ?: it.title?.native ?: "Unknown",
                            imageUrl = it.bannerImage ?: "",
                            rating = it.averageScore ?: 0,
                            description = it.description ?: "",
                            season = it.season ?: MediaSeason.UNKNOWN__,
                            genres = it.genres?.mapNotNull { genre -> genre } ?: emptyList(),
                            coverImage = it.coverImage?.large ?: "",
                            seasonYear = it.seasonYear ?: 1500
                        )
                    }
                } ?: emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun applyFilters() {
        isLoading = true
        coroutineScope.launch {
            try {
                val response = AnimeData().getByFilter(
                    season = filters.season,
                    seasonYear = filters.year,
                    status = filters.status,
                    genre = "",
                    averageScore_greater = (filters.minRating * 10).toInt(),
                    perPage = filters.resultSize
                )

                animeResults = response.data?.Page?.media?.mapNotNull { media ->
                    media?.let {
                        Anime(
                            id = it.id,
                            title = it.title?.english ?: it.title?.native ?: "Unknown",
                            imageUrl = it.bannerImage ?: "",
                            rating = it.averageScore ?: 0,
                            description = it.description ?: "",
                            season = it.season ?: MediaSeason.UNKNOWN__,
                            genres = it.genres?.mapNotNull { genre -> genre } ?: emptyList(),
                            coverImage = it.coverImage?.large ?: "",
                            seasonYear = it.seasonYear ?: 1500
                        )
                    }
                } ?: emptyList()
                Logger.log(LogLevel.INFO, "Browse", "Applied filters: $filters")
            } finally {
                isLoading = false
            }
        }
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AikoTheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompactSearchBar(
                    value = filters.searchQuery,
                    onValueChange = {
                        filters = filters.copy(searchQuery = it)
                    },
                    modifier = Modifier.weight(2f)
                )

                CompactDropdown(
                    title = when (filters.season) {
                        MediaSeason.UNKNOWN__ -> "Season"
                        else -> filters.season.name.capitalize()
                    },
                    expanded = seasonExpanded,
                    onExpandChange = { seasonExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    DropdownMenuItem(onClick = {
                        filters = filters.copy(season = MediaSeason.UNKNOWN__)
                        seasonExpanded = false
                    }) {
                        Text("Any", color = Color.White)
                    }

                    MediaSeason.entries.filter { it != MediaSeason.UNKNOWN__ }.forEach { season ->
                        DropdownMenuItem(onClick = {
                            filters = filters.copy(season = season)
                            seasonExpanded = false
                        }) {
                            Text(season.name.capitalize(), color = Color.White)
                        }
                    }
                }

                CompactDropdown(
                    title = filters.year?.toString() ?: "Year",
                    expanded = yearExpanded,
                    onExpandChange = { yearExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    DropdownMenuItem(onClick = {
                        filters = filters.copy(year = null)
                        yearExpanded = false
                    }) {
                        Text("Any", color = Color.White)
                    }

                    years.forEach { year ->
                        DropdownMenuItem(onClick = {
                            filters = filters.copy(year = year)
                            yearExpanded = false
                        }) {
                            Text(year.toString(), color = Color.White)
                        }
                    }
                }

                CompactDropdown(
                    title = when {
                        filters.genres.isEmpty() -> "Genre"
                        filters.genres.size == 1 -> filters.genres.first().displayName
                        else -> "${filters.genres.size} Genres"
                    },
                    expanded = genreExpanded,
                    onExpandChange = { genreExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    AnimeGenre.values().forEach { genre ->
                        val isSelected = filters.genres.contains(genre)
                        DropdownMenuItem(onClick = {
                            val newSelection = if (isSelected) {
                                filters.genres - genre
                            } else {
                                filters.genres + genre
                            }
                            filters = filters.copy(genres = newSelection)
                        }) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null,
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = AikoTheme.primary,
                                        uncheckedColor = Color.White.copy(alpha = 0.7f)
                                    )
                                )
                                Text(genre.displayName, color = Color.White)
                            }
                        }
                    }

                    Divider(color = Color.Gray.copy(alpha = 0.3f))

                    DropdownMenuItem(onClick = {
                        filters = filters.copy(genres = emptyList())
                        genreExpanded = false
                    }) {
                        Text("Clear All", color = AikoTheme.primary)
                    }
                }

                CompactDropdown(
                    title = if (filters.minRating > 0f) "${filters.minRating}+" else "Rating",
                    expanded = ratingExpanded,
                    onExpandChange = { ratingExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    DropdownMenuItem(onClick = {
                        filters = filters.copy(minRating = 0f)
                        ratingExpanded = false
                    }) {
                        Text("Any", color = Color.White)
                    }

                    ratingOptions.forEach { rating ->
                        DropdownMenuItem(onClick = {
                            filters = filters.copy(minRating = rating.toFloat())
                            ratingExpanded = false
                        }) {
                            Text("$rating+", color = Color.White)
                        }
                    }
                }

                CompactDropdown(
                    title = when (filters.status) {
                        MediaStatus.UNKNOWN__ -> "Status"
                        else -> filters.status.name.capitalize()
                    },
                    expanded = statusExpanded,
                    onExpandChange = { statusExpanded = it },
                    modifier = Modifier.weight(1f)
                ) {
                    DropdownMenuItem(onClick = {
                        filters = filters.copy(status = MediaStatus.UNKNOWN__)
                        statusExpanded = false
                    }) {
                        Text("Any", color = Color.White)
                    }

                    MediaStatus.entries.filter { it != MediaStatus.UNKNOWN__ }.forEach { status ->
                        DropdownMenuItem(onClick = {
                            filters = filters.copy(status = status)
                            statusExpanded = false
                        }) {
                            Text(status.name.capitalize(), color = Color.White)
                        }
                    }
                }

                CompactDropdown(
                    title = "${filters.resultSize}",
                    expanded = resultSizeExpanded,
                    onExpandChange = { resultSizeExpanded = it },
                    modifier = Modifier.weight(0.8f)
                ) {
                    resultSizeOptions.forEach { size ->
                        DropdownMenuItem(onClick = {
                            filters = filters.copy(resultSize = size)
                            resultSizeExpanded = false
                        }) {
                            Text("$size", color = Color.White)
                        }
                    }
                }

                Button(
                    onClick = { applyFilters() },
                    modifier = Modifier
                        .height(40.dp)
                        .weight(0.8f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Apply", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${animeResults.size} results",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AikoTheme.primary)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(8),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(animeResults) { anime ->
                        AnimeCard(
                            anime = anime,
                            onClick = { onAnimeSelected(anime) },
                            cardWidth = 50.dp,
                            cardHeight = 300.dp
                        )
                    }
                }
            }
        }
    }
}

fun String.capitalize(): String {
    return this.lowercase().replaceFirstChar { it.uppercase() }
}