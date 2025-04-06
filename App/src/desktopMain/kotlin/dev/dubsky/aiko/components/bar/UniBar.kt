package dev.dubsky.aiko.components.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Maximize
import androidx.compose.material.icons.filled.Minimize
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.dubsky.aiko.components.button.ActionButton
import dev.dubsky.aiko.resources.Res
import dev.dubsky.aiko.resources.discord
import dev.dubsky.aiko.screens.Screens
import org.jetbrains.compose.resources.painterResource

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
                .height(60.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("A")
                    }
                    append("iko")
                },
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp)
            )

            Spacer(modifier = Modifier.width(60.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
            ) {
                NavItem("Home", currentScreen == Screens.Home) {
                    onScreenSelected(Screens.Home)
                }
                NavItem("Browse", currentScreen == Screens.Browse) {
                    onScreenSelected(Screens.Browse)
                }
                NavItem("List", currentScreen == Screens.List) {
                    onScreenSelected(Screens.List)
                }
                NavItem("Profile", currentScreen == Screens.PROFILE) {
                    onScreenSelected(Screens.PROFILE)
                }
                NavItem("Player", currentScreen == Screens.PLAYER) {
                    onScreenSelected(Screens.PLAYER)
                }
                NavItem("Settings", currentScreen == Screens.Settings) {
                    onScreenSelected(Screens.Settings)
                }

                Spacer(modifier = Modifier.width(30.dp))

                Box(
                    modifier = Modifier
                        .height(60.dp)
                ) {
                    Button(
                        onClick = { /* ... */ },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .height(40.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary,
                                        MaterialTheme.colorScheme.primary
                                    ),
                                    startX = 0f,
                                    endX = 200f
                                ),
                                shape = RoundedCornerShape(25.dp)
                            )
                            .width(IntrinsicSize.Max),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.discord),
                                contentDescription = "Discord",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Join Our Discord")
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(Icons.Filled.Minimize, onClick = onMinimizeClick)
                ActionButton(Icons.Filled.Window, onClick = onMaximizeClick)
                ActionButton(Icons.Filled.Close, onClick = onCloseClick)
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.primary),
        )
    }
}

@Composable
fun NavItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(100.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Spacer(
                    modifier = Modifier
                        .height(5.dp)
                        .width(80.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary,
                                    MaterialTheme.colorScheme.primary
                                ),
                                startX = 0f,
                                endX = 50f
                            ),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .clip(RoundedCornerShape(25.dp))
                )
            }
        }
    }
}