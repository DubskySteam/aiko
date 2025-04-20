package dev.dubsky.aiko.components.util

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.dubsky.aiko.config.AppVersion

@Composable
fun VersionInfoRow(
    updateAvailable: AppVersion.VersionCheckResult,
    onUpdateClick: () -> Unit,
    onPatchNotesClick: () -> Unit
) {
    var showVersionMenu by remember {
        mutableStateOf(
            false
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            TextButton(
                onClick = { showVersionMenu = true },
                modifier = Modifier.height(36.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Version: " + AppVersion.CURRENT_VERSION,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Version menu",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = showVersionMenu,
                onDismissRequest = { showVersionMenu = false }
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Current: ${AppVersion.CURRENT_VERSION}",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    onClick = { showVersionMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("View Patch Notes") },
                    onClick = {
                        onPatchNotesClick()
                        showVersionMenu = false
                    }
                )
            }
        }

        Button(
            onClick = onUpdateClick,
            enabled = updateAvailable != AppVersion.VersionCheckResult.BetaVersion && updateAvailable != AppVersion.VersionCheckResult.UpToDate,
            modifier = Modifier.height(36.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (updateAvailable is AppVersion.VersionCheckResult.UpdateAvailable) Color.Red
                else MaterialTheme.colorScheme.surface,
                contentColor = if (updateAvailable is AppVersion.VersionCheckResult.UpdateAvailable) MaterialTheme.colorScheme.onTertiary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Text(
                text = when (updateAvailable) {
                    is AppVersion.VersionCheckResult.UpdateAvailable -> "Update"
                    AppVersion.VersionCheckResult.BetaVersion -> "Beta"
                    else -> "Up to date"
                },
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}