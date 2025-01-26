package org.turter.patrocl.presentation.orders.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun InterceptedAddingDialog(
    warningType: AddingWarningType,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val message = when(warningType) {
        is AddingWarningType.OnStop -> "Позиция на стопе. Уверены, что хотите добавить?"
        is AddingWarningType.LowRemain -> "Осталось ${warningType.count} шт. Уверены, что хотите добавить?"
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Header icon"
            )
        },
        title = {
            Text(message)
        },
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