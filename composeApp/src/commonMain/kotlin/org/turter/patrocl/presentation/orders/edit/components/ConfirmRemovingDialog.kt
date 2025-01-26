package org.turter.patrocl.presentation.orders.edit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.order.Order
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