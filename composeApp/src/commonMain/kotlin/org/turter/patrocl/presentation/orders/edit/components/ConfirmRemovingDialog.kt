package org.turter.patrocl.presentation.orders.edit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.turter.patrocl.presentation.components.FloatNaturalInput

@Composable
fun RemoveSingleOrderItemDialog(
    expanded: Boolean,
    itemName: String?,
    initialQuantity: Float = 1f,
    maxQuantity: Float,
    onDismiss: () -> Unit,
    onConfirm: (quantity: Float) -> Unit
) {
    if (expanded && itemName != null) {
        var quantity by remember { mutableFloatStateOf(initialQuantity) }

        AlertDialog(
            onDismissRequest = onDismiss,
            icon = { Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete icon") },
            title = { Text("Удалить позицию \"$itemName\"?") },
            text = {
                FloatNaturalInput(
                    modifier = Modifier.fillMaxWidth(),
                    initialValue = initialQuantity,
                    validate = { it <= maxQuantity },
                    onValueChange = { quantity = it }
                )
            },
            confirmButton = {
                TextButton(
                    enabled = quantity > 0 && quantity <= maxQuantity,
                    onClick = {
                        onConfirm(quantity)
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