package dev.dubsky.aiko.components.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.dubsky.aiko.components.button.ActionButton
import dev.dubsky.aiko.screens.Screens

@Composable
fun UnifiedBar(
    currentScreen: Screens,
    onScreenSelected: (Screens) -> Unit,
    onMinimizeClick: () -> Unit,
    onMaximizeClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xFF0F141A))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colors.primary)) {
                        append("A")
                    }
                    append("iko")
                },
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(60.dp))

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem("Home", Icons.Default.Home, currentScreen == Screens.Home) {
                    onScreenSelected(Screens.Home)
                }
                NavItem("Browse", Icons.Default.Search, currentScreen == Screens.Browse) {
                    onScreenSelected(Screens.Browse)
                }
                NavItem("List", Icons.AutoMirrored.Filled.List, currentScreen == Screens.List) {
                    onScreenSelected(Screens.List)
                }
                NavItem("Profile", Icons.Default.VerifiedUser, currentScreen == Screens.PROFILE) {
                    onScreenSelected(Screens.PROFILE)
                }
                NavItem("Player", Icons.Default.PlayCircleFilled, currentScreen == Screens.PLAYER) {
                    onScreenSelected(Screens.PLAYER)
                }
                NavItem("Settings", Icons.Default.Settings, currentScreen == Screens.Settings) {
                    onScreenSelected(Screens.Settings)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(Color.Green, onClick = onMinimizeClick)
                ActionButton(Color.Yellow, onClick = onMaximizeClick)
                ActionButton(Color.Red, onClick = onCloseClick)
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colors.primary)
        )
    }
}

@Composable
fun NavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isSelected) MaterialTheme.colors.primary else Color.White.copy(alpha = 0.8f)
    val iconColor = if (isSelected) MaterialTheme.colors.primary else Color.White.copy(alpha = 0.8f)

    Row(
        modifier = Modifier.clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = iconColor)
        Text(text = label, color = textColor, fontSize = 12.sp)
    }
}