package org.turter.patrocl.presentation.orders.create.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import org.turter.patrocl.presentation.orders.create.CreateOrderScreenState
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.AddNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.CloseNewOrderItemDialog
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.CreateOrUpdateNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.CreateOrder
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.IncreaseNewOrderItemQuantity
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.OpenEditNewOrderItemDialog
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.RemoveNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.SelectTable
import org.turter.patrocl.presentation.orders.create.CreateOrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderComponent(
    vm: CreateOrderViewModel,
    currentScreenState: CreateOrderScreenState.CreateNewOrder,
    paddingValues: PaddingValues = PaddingValues()
) {
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
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(text = "Создание заказа")

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
                onSelectTable = { vm.sendEvent(SelectTable(it)) }
            )

            OutlinedButton(
                modifier = Modifier
                    .width(100.dp)
                    .height(TextFieldDefaults.MinHeight),
                shape = RoundedCornerShape(4.dp),
                enabled = currentScreenState.selectedTable != null,
                onClick = { showBottomSheet = true },
            ) {
                Text(text = "Меню", maxLines = 1)
            }
        }

        HorizontalDivider(modifier = Modifier.padding(0.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items = newOrderItems, key = { it.uuid }) { orderItem ->
                SwipeToDismissComponent(
                    onStartToEnd = { vm.sendEvent(RemoveNewOrderItem(orderItem)) },
                    onEndToStart = { vm.sendEvent(RemoveNewOrderItem(orderItem)) }
                ) {
                    OrderItemCard(
                        item = orderItem,
                        enabled = true,
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
                enabled = currentScreenState.selectedTable != null,
                onClick = {
                    vm.sendEvent(CreateOrder)
                },
            ) {
                Text(text = "Создать")
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
                    vm.sendEvent(AddNewOrderItem(dishId = dish.id, dishName = dish.name))
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
}