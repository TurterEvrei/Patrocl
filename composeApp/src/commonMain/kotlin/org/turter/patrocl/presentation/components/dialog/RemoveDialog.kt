package org.turter.patrocl.presentation.components.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun RemoveItemDialog(
    expanded: Boolean,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (expanded) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = { Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete icon") },
            title = { Text(title) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    }
                ) {
                    Text("Подтвердить".uppercase())
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Закрыть".uppercase())
                }
            }
        )
    }
}

@Composable
fun RemoveItemsDialog(
    expanded: Boolean,
    count: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (expanded) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = { Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete icon") },
            title = { Text("Удалить выбранные элементы ($count шт.)?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    }
                ) {
                    Text("Подтвердить".uppercase())
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Закрыть".uppercase())
                }
            }
        )
    }
}