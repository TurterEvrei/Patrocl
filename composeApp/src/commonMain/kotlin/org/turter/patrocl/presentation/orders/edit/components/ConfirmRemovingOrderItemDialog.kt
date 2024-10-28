package org.turter.patrocl.presentation.orders.edit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.order.SavedOrderItem

@Composable
fun ConfirmRemovingOrderItemDialog(
    expanded: Boolean,
    orderItem: SavedOrderItem?,
    onDismiss: () -> Unit,
    onConfirm: (orderItem: SavedOrderItem, quantityToRemove: Float) -> Unit
) {
    if (expanded) {
        var quantity by remember { mutableFloatStateOf(orderItem?.quantity ?: 1f) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Confirm remove item") },
            text = {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = if (quantity == 0f) "" else quantity.toString(),
                    onValueChange = {
                        if (it.toFloatOrNull() != null && it.isNotEmpty()) {
                            val res = it.toFloat()
//                            if (res > 0 && res <= (orderItem?.quantity
//                                    ?: Float.MAX_VALUE)
//                            ) quantity = res
                            quantity = res
                        }
                    },
                    label = { Text("Кол-во") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        if (orderItem != null
                            && quantity > 0
                            && quantity <= orderItem.quantity
                        ) {
                            onConfirm(orderItem, quantity)
                        }
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                Button(
                    shape = RoundedCornerShape(4.dp),
                    onClick = onDismiss
                ) {
                    Text("Отменить")
                }
            }
        )
    }
}