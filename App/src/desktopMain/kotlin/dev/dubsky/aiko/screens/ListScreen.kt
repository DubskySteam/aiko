package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.apollographql.apollo.api.ApolloResponse
import dev.dubsky.aiko.api.AnimeData
import dev.dubsky.aiko.graphql.UserInfoExtendedQuery
import dev.dubsky.aiko.logging.LogLevel
import dev.dubsky.aiko.logging.Logger
import kotlinx.coroutines.launch

data class AnimeListItem(
    val id: Int? = 0,
    val title: String? = "Unknown",
    val episodes: Int? = 0,
    val score: Float? = 0f,
    val progress: Int? = 0
)

data class AnimeListCategory(
    val status: String? = "Unknown category",
    val entries: List<AnimeListItem?>? = emptyList()
)

private fun parseAnimeList(response: ApolloResponse<UserInfoExtendedQuery.Data>): List<AnimeListCategory> {
    return response.data!!.MediaListCollection?.lists?.map { list ->
        AnimeListCategory(
            status = list?.status.toString(),
            entries = list?.entries?.map { entry ->
                AnimeListItem(
                    id = entry?.media?.id,
                    title = entry?.media?.title?.english ?: entry?.media?.title?.native,
                    episodes = entry?.media?.episodes,
                    score = entry?.score?.toFloat(),
                    progress = entry?.progress
                )
            }
        )
    } ?: emptyList()
}

@Composable
fun AnimeListScreen() {
    val coroutineScope = rememberCoroutineScope()
    var tables by remember { mutableStateOf(emptyList<AnimeListCategory>()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val categories = listOf("Watching", "Finished", "Set to watch", "Canceled")
    val apiCategories = listOf("CURRENT", "COMPLETED", "PLANNING", "DROPPED")

    val reorderedTables = remember(tables) {
        apiCategories.map { apiCategory ->
            tables.find { it.status == apiCategory } ?: AnimeListCategory(status = apiCategory)
        }
    }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                Logger.log(LogLevel.INFO, "AnimeList", "Attempting to parse user list information")
                val response = AnimeData().GetUserAnimeList()
                if (response.data != null) {
                    Logger.log(LogLevel.INFO, "AnimeList", "Data found... parsing")
                    tables = parseAnimeList(response)
                }
            } catch (e: Exception) {
                Logger.log(LogLevel.ERROR, "AnimeList", "Failed to parse user list information")
            } finally {
                isLoading = false
                Logger.log(LogLevel.INFO, "AnimeList", "Fetch and parse complete")
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    categories.forEachIndexed { index, category ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(text = category) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp)
            ) {
                val selectedCategory = reorderedTables.getOrNull(selectedTabIndex)
                if (selectedCategory != null) {
                    AnimeTable(
                        category = selectedCategory,
                        isCurrentTab = selectedTabIndex == 0
                    )
                }
            }
        }
    }
}

@Composable
fun AnimeTable(category: AnimeListCategory, isCurrentTab: Boolean) {
    var sortBy by remember { mutableStateOf<SortBy?>(null) }
    var isAscending by remember { mutableStateOf(true) }

    val sortedEntries = remember(category.entries, sortBy, isAscending) {
        when (sortBy) {
            SortBy.TITLE -> if (isAscending) category.entries?.sortedBy { it?.title }
            else category.entries?.sortedByDescending { it?.title }

            SortBy.SCORE -> if (isAscending) category.entries?.sortedBy { it?.score }
            else category.entries?.sortedByDescending { it?.score }

            SortBy.PROGRESS -> if (isAscending) category.entries?.sortedBy { it?.progress }
            else category.entries?.sortedByDescending { it?.progress }

            SortBy.EPISODES -> if (isAscending) category.entries?.sortedBy { it?.episodes }
            else category.entries?.sortedByDescending { it?.episodes }

            else -> category.entries
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TableHeader("Title", SortBy.TITLE, sortBy, isAscending, Modifier.weight(2.5f)) {
                sortBy = SortBy.TITLE
                isAscending = if (sortBy == SortBy.TITLE) !isAscending else true
            }
            TableHeader("Score", SortBy.SCORE, sortBy, isAscending, Modifier.weight(0.7f)) {
                sortBy = SortBy.SCORE
                isAscending = if (sortBy == SortBy.SCORE) !isAscending else true
            }
            TableHeader("Progress", SortBy.PROGRESS, sortBy, isAscending, Modifier.weight(1f)) {
                sortBy = SortBy.PROGRESS
                isAscending = if (sortBy == SortBy.PROGRESS) !isAscending else true
            }
            TableHeader("Episodes", SortBy.EPISODES, sortBy, isAscending, Modifier.weight(0.7f)) {
                sortBy = SortBy.EPISODES
                isAscending = if (sortBy == SortBy.EPISODES) !isAscending else true
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(sortedEntries ?: emptyList()) { entry ->
                if (entry != null) {
                    AnimeTableRow(entry, isCurrentTab)
                }
            }
        }
    }
}

@Composable
fun TableHeader(
    text: String,
    sortKey: SortBy,
    currentSort: SortBy?,
    isAscending: Boolean,
    modifier: Modifier,
    onSort: () -> Unit
) {
    val isSorted = currentSort == sortKey
    Box(
        modifier = modifier
            .clickable { onSort() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = text,
                fontWeight = if (isSorted) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center
            )
            if (isSorted) {
                Icon(
                    imageVector = if (isAscending) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                    contentDescription = "Sorted",
                    tint = MaterialTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
fun AnimeTableRow(entry: AnimeListItem, isCurrentTab: Boolean) {
    var showEditDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit",
            modifier = Modifier
                .size(24.dp)
                .clickable { showEditDialog = true },
            tint = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = entry.title ?: "Unknown",
            modifier = Modifier.weight(2.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = entry.score?.toString() ?: "-",
            modifier = Modifier.weight(0.7f),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = entry.progress?.toString() ?: "-",
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
            if (isCurrentTab) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Episode",
                    modifier = Modifier.size(20.dp).clickable {
                        if (entry.progress!! < entry.episodes!!) {
                            Logger.log(LogLevel.INFO, "AnimeList", "Adding +1 episode to {${entry.title}}")
                        } else {
                            Logger.log(
                                LogLevel.WARN,
                                "AnimeList",
                                "Failed to +1 episode to {${entry.title}} - Already at latest episode"
                            )
                        }
                    },
                    tint = MaterialTheme.colors.primary
                )
            }
        }

        Text(
            text = entry.episodes?.toString() ?: "-",
            modifier = Modifier.weight(0.7f),
            textAlign = TextAlign.Center,
            color = Color.Gray
        )
    }

    if (showEditDialog) {
        var episode by remember { mutableStateOf(entry.episodes.toString()) }
        var score by remember { mutableStateOf(entry.score.toString()) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Update your data for: ${entry.title}") },
            text = {
                Column {
                    // Episode Input
                    Row {
                        Text("Episode")
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = episode,
                            onValueChange = { episode = it },
                            label = { Text("Episode") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(100.dp)
                        )
                        Text("of ${entry.episodes}")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Score Input
                    Row {
                        Text("Score")
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = score,
                            onValueChange = { score = it },
                            label = { Text("Score") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(100.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    // Save logic here
                    val updatedEpisode = episode.toIntOrNull() ?: entry.episodes
                    val updatedScore = score.toFloatOrNull() ?: entry.score
                    // Call a function to update the entry with the new values
                    Logger.log(LogLevel.INFO, "ListScreen", "Updated information for {${entry.title}}")
                    updateEntry(entry.copy(episodes = updatedEpisode, score = updatedScore))
                    showEditDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Button(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun updateEntry(entry: AnimeListItem) {

}

enum class SortBy {
    TITLE, SCORE, PROGRESS, EPISODES
}