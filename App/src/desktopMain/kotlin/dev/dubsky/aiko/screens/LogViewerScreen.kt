package dev.dubsky.aiko.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun LogViewerScreen() {
    val logDirectory = "${System.getenv("AppData")}/Aiko/logs"
    val logFiles = remember { File(logDirectory).listFiles()?.toList()?.sortedByDescending { it.name } ?: emptyList() }
    var selectedLogFile by remember { mutableStateOf(logFiles.firstOrNull()) }
    var showLogSelector by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFF121212)
    val surfaceColor = Color(0xFF1E1E1E)
    val textColor = Color.White

    MaterialTheme(
        colors = darkColors(
            background = backgroundColor,
            surface = surfaceColor,
            onSurface = textColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Button(
                onClick = { showLogSelector = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedLogFile?.name ?: "Select a log file")
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Open log file selector")
            }

            Spacer(modifier = Modifier.height(16.dp))

            selectedLogFile?.let { file ->
                val logLines = remember { file.readLines() }
                val scrollState = rememberLazyListState()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(surfaceColor, RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    state = scrollState
                ) {
                    items(logLines) { line ->
                        LogLine(line)
                    }
                }
            }
        }

        if (showLogSelector) {
            LogSelectorDialog(
                logFiles = logFiles,
                onDismiss = { showLogSelector = false },
                onLogSelected = { file ->
                    selectedLogFile = file
                    showLogSelector = false
                }
            )
        }
    }
}

@Composable
fun LogSelectorDialog(
    logFiles: List<File>,
    onDismiss: () -> Unit,
    onLogSelected: (File) -> Unit
) {
    var filterText by remember { mutableStateOf("") }
    val filteredLogs = logFiles.filter { it.name.contains(filterText, ignoreCase = true) }.take(5)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Log File") },
        text = {
            Column {
                // Date filter input
                OutlinedTextField(
                    value = filterText,
                    onValueChange = { filterText = it },
                    label = { Text("Filter by date (dd-MM-yyyy)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(filteredLogs) { file ->
                        Button(
                            onClick = { onLogSelected(file) },
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        ) {
                            Text(file.name)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun LogLine(line: String) {
    val annotatedString = buildAnnotatedString {
        try {
            val timestamp = line.substringAfter("[").substringBefore("]")
            val level = line.substringAfter("[$timestamp] [").substringBefore("]")
            val className = line.substringAfter("[$level] [").substringBefore("]")
            val message = line.substringAfter("$className]: ")

            withStyle(SpanStyle(color = Color.Gray)) {
                append("[$timestamp] ")
            }
            withStyle(SpanStyle(color = when (level) {
                "INFO" -> Color.Green
                "WARN" -> Color.Yellow
                "ERROR" -> Color.Red
                else -> Color.White
            })) {
                append("[$level] ")
            }
            withStyle(SpanStyle(color = Color.Cyan)) {
                append("[$className]: ")
            }
            append(message)
        } catch (e: Exception) {
            append(line)
        }
    }

    Text(
        text = annotatedString,
        modifier = Modifier.padding(4.dp),
        color = Color.White
    )
}
