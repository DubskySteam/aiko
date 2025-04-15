package dev.dubsky.aiko.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDialogOutlinedButton(
    buttonText: String = "Click Me",
    dialogTitle: String = "Confirm Action",
    dialogMessage: String = "Are you sure you want to proceed?",
    confirmText: String = "Confirm",
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val showDialogState = remember { mutableStateOf(false) }
    var showDialog by showDialogState

    Column {
        OutlinedButton(
            onClick = { showDialog = true },
            modifier = modifier
                .height(40.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface,
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(buttonText)
            }
        }

        if (showDialog) {
            CustomDialog(
                title = dialogTitle,
                message = dialogMessage,
                confirmText = confirmText,
                cancelText = "Cancel",
                onConfirm = onConfirm,
                onDismiss = { showDialog = false }
            )
        }
    }
}
