package org.turter.patrocl.presentation.orders.edit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.turter.patrocl.presentation.components.SwipeToDismissComponent
import org.turter.patrocl.presentation.orders.common.EditOrderItemDialog
import org.turter.patrocl.presentation.orders.common.MenuSelectorComponent
import org.turter.patrocl.presentation.orders.common.OrderItemCard
import org.turter.patrocl.presentation.orders.common.SelectTableComponent
import org.turter.patrocl.presentation.orders.edit.EditOrderScreenState
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.AddNewOrderItem
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.CloseNewOrderItemDialog
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.CloseRemovingSavedOrderItemDialog
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.CreateOrUpdateNewOrderItem
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.IncreaseNewOrderItemQuantity
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.OpenEditNewOrderItemDialog
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.OpenRemovingSavedOrderItemDialog
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.RemoveNewOrderItem
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.SaveOrder
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.SelectTable
import org.turter.patrocl.presentation.orders.edit.EditOrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrderComponent(
    vm: EditOrderViewModel,
    currentScreenState: EditOrderScreenState.UpdateSavedOrder,
    enableSelectTable: Boolean = true
) {
    val originOrder = currentScreenState.order
    val orderItems = originOrder.items
    val newOrderItems = currentScreenState.newOrderItems
    val category = currentScreenState.menuData.category
    val dishes = currentScreenState.menuData.dishes
    val modifiers = currentScreenState.menuData.modifiers
    val tables = currentScreenState.tables

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(text = "Редактирование заказа: ${originOrder.name}")

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            SelectTableComponent(
                modifier = Modifier
                    .weight(1f),
                tables = tables,
                selectedTable = currentScreenState.selectedTable,
                enabled = enableSelectTable,
                onSelectTable = { vm.sendEvent(SelectTable(it)) }
            )

            OutlinedButton(
                modifier = Modifier
                    .width(100.dp)
                    .height(TextFieldDefaults.MinHeight),
                shape = RoundedCornerShape(4.dp),
//                enabled = currentScreenState.selectedTable != null,
                onClick = { showBottomSheet = true },
            ) {
                Text(text = "Меню", maxLines = 1)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(0.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            //old items
            items(items = orderItems, key = { it.uuid }) { orderItem ->
                if (orderItem.quantity > 0) OrderItemCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    item = orderItem,
                    onLongClick = { vm.sendEvent(OpenRemovingSavedOrderItemDialog(orderItem)) },
                    onClick = {
                        vm.sendEvent(
                            AddNewOrderItem(
                                dishId = orderItem.dishId,
                                dishName = orderItem.dishName
                            )
                        )
                    }
                )
            }
            //new items
            items(items = newOrderItems, key = { it.uuid }) { orderItem ->
                if (orderItem.quantity > 0) SwipeToDismissComponent(
                    onStartToEnd = { vm.sendEvent(RemoveNewOrderItem(orderItem)) },
                    onEndToStart = { vm.sendEvent(RemoveNewOrderItem(orderItem)) }
                ) {
                    OrderItemCard(
                        item = orderItem,
                        onLongClick = { vm.sendEvent(OpenEditNewOrderItemDialog(orderItem)) },
                        onClick = { vm.sendEvent(IncreaseNewOrderItemQuantity(orderItem)) }
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(0.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
//                shape = RoundedCornerShape(4.dp),
                enabled =
//                currentScreenState.selectedTable != null &&
                newOrderItems.isNotEmpty(),
                onClick = { vm.sendEvent(SaveOrder) },
            ) {
                Text("Сохранить")
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxHeight(),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false }
        ) {
            MenuSelectorComponent(
                modifier = Modifier
                    .padding(6.dp),
                rootCategory = category,
                allDishes = dishes,
                onDishClick = { dish ->
                    vm.sendEvent(
                        AddNewOrderItem(
                            dishId = dish.id,
                            dishName = dish.name,
                        )
                    )
                }
            )
        }
    }

    EditOrderItemDialog(
        expanded = currentScreenState.expandedOrderItemDialog,
        orderItem = currentScreenState.newOrderItemForDialog,
        allModifiers = modifiers,
        allDishes = dishes,
        onConfirm = { vm.sendEvent(CreateOrUpdateNewOrderItem(it)) },
        onDismiss = { vm.sendEvent(CloseNewOrderItemDialog) }
    )

    ConfirmRemovingOrderItemDialog(
        expanded = currentScreenState.expandedConfirmRemovingOrderItem,
        orderItem = currentScreenState.orderItemForRemoving,
        onConfirm = { item, quantity ->
            vm.sendEvent(
                EditOrderUiEvent.ConfirmRemovingSavedOrderItem(
                    orderItem = item,
                    quantity = quantity
                )
            )
        },
        onDismiss = { vm.sendEvent(CloseRemovingSavedOrderItemDialog) }
    )
}