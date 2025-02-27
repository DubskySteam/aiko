package dev.dubsky.aiko.components.bar

import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.dubsky.aiko.screens.Screens

@Composable
fun NavBar(
    currentScreen: Screens,
    onScreenSelected: (Screens) -> Unit,
    menuSize: Dp
) {
    BottomNavigation(
        modifier = Modifier.height(menuSize),
        backgroundColor = Color(0xFF0F141A),
        contentColor = Color.White,
        elevation = 10.dp
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentScreen == Screens.Home,
            onClick = { onScreenSelected(Screens.Home) },
            selectedContentColor = Color(0xFF4CA1AF),
            unselectedContentColor = Color.White.copy(alpha = 1f)
        )

        BottomNavigationItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Browse") },
            label = { Text("Browse") },
            selected = currentScreen == Screens.Browse,
            onClick = { onScreenSelected(Screens.Browse) },
            selectedContentColor = Color(0xFF4CA1AF),
            unselectedContentColor = Color.White.copy(alpha = 1f)
        )

        BottomNavigationItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "List") },
            label = { Text("List") },
            selected = currentScreen == Screens.List,
            onClick = { onScreenSelected(Screens.List) },
            selectedContentColor = Color(0xFF4CA1AF),
            unselectedContentColor = Color.White.copy(alpha = 1f)
        )

        BottomNavigationItem(
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Player") },
            label = { Text("Player") },
            selected = currentScreen == Screens.Player,
            onClick = { onScreenSelected(Screens.Player) },
            selectedContentColor = Color(0xFF4CA1AF),
            unselectedContentColor = Color.White.copy(alpha = 1f)
        )

        BottomNavigationItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = currentScreen == Screens.Settings,
            onClick = { onScreenSelected(Screens.Settings) },
            selectedContentColor = Color(0xFF4CA1AF),
            unselectedContentColor = Color.White.copy(alpha = 1f)
        )
    }
}