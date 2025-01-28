package org.turter.patrocl.presentation.orders.item.new.edit.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.turter.patrocl.domain.model.order.NewOrderItem

@Composable
fun CreateCommentDialog(
    modifiers: List<NewOrderItem.Modifier>,
    onConfirm: (comment: String) -> Unit,
    onDismiss: () -> Unit
) {
    val comments = modifiers.map { it.content }.filter { it.isNotEmpty() }.toList()
    var newComment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Создание комментария") },
        icon = { Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit icon") },
        text = {
            OutlinedTextField(
                value = newComment,
                onValueChange = { newComment = it },
                label = { Text("Введите комментарий") },
                maxLines = 5
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        confirmButton = {
            TextButton(
                enabled = newComment.isNotEmpty() && !comments.contains(newComment),
                onClick = {
                    if (newComment.isNotEmpty() && !comments.contains(newComment)) {
                        onConfirm(newComment.trim().replace("\\s+".toRegex(), " "))
                        onDismiss()
                        newComment = ""
                    }
                }
            ) {
                Text("Сохранить".uppercase())
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                newComment = ""
            }) {
                Text("Отмена".uppercase())
            }
        },
    )
}