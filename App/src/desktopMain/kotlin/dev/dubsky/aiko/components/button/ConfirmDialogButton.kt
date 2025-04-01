package dev.dubsky.aiko.components.button

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun ConfirmDialogButton(
    buttonText: String = "Click Me",
    dialogTitle: String = "Confirm Action",
    dialogMessage: String = "Are you sure you want to proceed?",
    confirmText: String = "Confirm",
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val showDialogState = remember { mutableStateOf(false) }
    var showDialog by showDialogState

    Column {
        Button(
            onClick = { showDialog = true },
            modifier = modifier
        ) {
            Text(buttonText)
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
