package org.turter.patrocl.presentation.orders.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.order.ItemModifier
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.source.Dish
import org.turter.patrocl.domain.model.source.DishModifier
import org.turter.patrocl.presentation.components.DropDownMenuWithFilter

@Composable
fun EditOrderItemDialog(
    expanded: Boolean,
    orderItem: NewOrderItem?,
    allModifiers: List<DishModifier>,
    allDishes: List<Dish>,
    onDismiss: () -> Unit,
    onConfirm: (orderItem: NewOrderItem) -> Unit
) {
    if (expanded) {
        var selectedDish: Dish? by remember {
            mutableStateOf(allDishes.find {
                it.id == orderItem?.dishId
            })
        }
        var quantity by remember { mutableFloatStateOf(orderItem?.quantity ?: 1f) }
        var modifiers by remember { mutableStateOf(orderItem?.modifiers ?: emptyList()) }
        var newModifier by remember { mutableStateOf<DishModifier?>(null) }
        var newModifierQuantity by remember { mutableIntStateOf(1) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Edit Order Item") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DropDownMenuWithFilter(
                        selectedItem = selectedDish,
                        items = allDishes,
                        limit = 30,
                        label = "Dish",
                        getName = { name }
                    ) {
                        selectedDish = it
                    }

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = quantity.toString(),
                        onValueChange = {
                            if (it.toFloatOrNull() != null && it.isNotEmpty()) {
                                val res = it.toFloat()
                                if (res > 0) quantity = res
                            }
                        },
                        label = { Text("Кол-во") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Text("Модификаторы:")

                    DropDownMenuWithFilter(
                        selectedItem = newModifier,
                        items = allModifiers,
                        limit = 30,
                        label = "Modifier",
                        getName = { name }
                    ) {
                        newModifier = it
                    }

                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth(0.7f),
                            value = newModifierQuantity.toString(),
                            onValueChange = {
                                if (it.toIntOrNull() != null && it.isNotEmpty()) {
                                    val res = it.toInt()
                                    if (res > 0) newModifierQuantity = res
                                }
                            },
                            label = { Text("Кол-во") },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        OutlinedIconButton(
                            modifier = Modifier
                                .height(OutlinedTextFieldDefaults.MinHeight)
                                .weight(1f),
                            shape = RoundedCornerShape(4.dp),
                            onClick = {
                                newModifier?.let {
                                    if (newModifierQuantity > 0) {
                                        modifiers = modifiers.toMutableList()
                                            .apply {
                                                add(
                                                    ItemModifier(
                                                        modifierId = it.id,
                                                        name = it.name,
                                                        quantity = newModifierQuantity
                                                    )
                                                )
                                            }
                                            .toList()
                                        newModifier = null
                                        newModifierQuantity = 1
                                    }
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Add")
                        }
                    }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(
                            items = modifiers,
                            key = { it.modifierId }
                        ) { modifierItem ->
                            HorizontalDivider()
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${modifierItem.name} x${modifierItem.quantity}")
                                IconButton(onClick = {
                                    modifiers = modifiers.toMutableList()
                                        .apply { remove(modifierItem) }
                                        .toList()
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(4.dp),
                    onClick = {
                        if (selectedDish != null && orderItem != null) {
                            orderItem.dishId = selectedDish!!.id
                            orderItem.dishName = selectedDish!!.name
                            orderItem.quantity = quantity
                            orderItem.modifiers = modifiers
                            onConfirm(orderItem)
                        }
                    }
                ) {
                    Text("Сохранить")
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