package org.turter.patrocl.presentation.orders.edit.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.order.Order
import org.turter.patrocl.presentation.components.FullscreenLoader
import org.turter.patrocl.presentation.components.SwipeToDismissComponent
import org.turter.patrocl.presentation.components.btn.ExpandableFAB
import org.turter.patrocl.presentation.components.btn.FABItem
import org.turter.patrocl.presentation.components.dialog.RemoveItemsDialog
import org.turter.patrocl.presentation.orders.common.AddingWarningType
import org.turter.patrocl.presentation.orders.common.EditNewOrderItemDialog
import org.turter.patrocl.presentation.orders.common.InterceptedAddingDialog
import org.turter.patrocl.presentation.orders.common.MenuSelectorComponent
import org.turter.patrocl.presentation.orders.common.NewOrderItemCard
import org.turter.patrocl.presentation.orders.common.OrderItemsGroupsDivider
import org.turter.patrocl.presentation.orders.common.SavedOrderItemCard
import org.turter.patrocl.presentation.orders.common.SessionCard
import org.turter.patrocl.presentation.orders.edit.EditOrderScreenState
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.SelectSavedItems
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.UnselectSavedOrderItems
import org.turter.patrocl.presentation.orders.edit.EditOrderViewModel
import org.turter.patrocl.presentation.orders.edit.Selected

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun EditOrderComponent(
    vm: EditOrderViewModel,
    state: EditOrderScreenState.Main
) {
    val originOrder = state.order
    val sessions = originOrder.sessions
    val newOrderItems = state.newOrderItems
    val selectedNewItem = state.getSelectedNewItem()
    val category = state.menuData.rootCategory
    val dishes = state.menuData.dishes
    val modifiers = state.menuData.modifiers
    val selected = state.selected

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    var isEditNewOrderItemDialogOpen by remember { mutableStateOf(false) }
    var isSavedItemRemoveDialogOpen by remember { mutableStateOf(false) }

    val defaultTopAppBarState = EditOrderTopAppBarState.Default(
        orderName = originOrder.name,
        waiterName = originOrder.waiter.name,
        onBack = { vm.sendEvent(EditOrderUiEvent.BackToOrders) },
        onMenuOpen = { coroutineScope.launch { scaffoldState.bottomSheetState.expand() } }
    )
    val topAppBarState = when (selected) {
        is Selected.None -> defaultTopAppBarState

        is Selected.NewItem -> {
            newOrderItems.find { it.uuid == selected.newItemUuid }?.let { item ->
                EditOrderTopAppBarState.NewItemSelected(
                    selectedItem = item,
                    onClose = { vm.sendEvent(EditOrderUiEvent.UnselectAllItems) },
                    onMoveUp = { vm.sendEvent(EditOrderUiEvent.MoveSelectedItemUp) },
                    onMoveDown = { vm.sendEvent(EditOrderUiEvent.MoveSelectedItemDown) },
                    onInfo = { isEditNewOrderItemDialogOpen = true }
                )
            } ?: defaultTopAppBarState
        }

        is Selected.SavedItems -> {
            selected.withSingleItem()?.let { item ->
                val targetDish = item.dishes.first()
                EditOrderTopAppBarState.SingleSavedItemSelected(
                    itemName = targetDish.name,
                    itemCount = targetDish.quantity,
                    onClose = { vm.sendEvent(EditOrderUiEvent.UnselectAllItems) },
                    onDelete = { isSavedItemRemoveDialogOpen = true }
                )
            } ?: EditOrderTopAppBarState.SavedItemsSelected(
                selectedItemsCount = selected.items.flatMap { it.dishes }.count(),
                onClose = { vm.sendEvent(EditOrderUiEvent.UnselectAllItems) },
                onDelete = { isSavedItemRemoveDialogOpen = true }
            )
        }
    }

    val fabItems = listOf(
        FABItem(
            icon = Icons.Default.Check,
            text = "Применить",
            action = { vm.sendEvent(EditOrderUiEvent.SaveOrderAndContinue) }
        ),
        FABItem(
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            text = "Сохранить",
            action = { vm.sendEvent(EditOrderUiEvent.SaveOrderAndGoToOrders) }
        )
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 40.dp,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MenuSelectorComponent(
                    rootCategory = category,
                    allDishes = dishes,
                    onDishClick = { dish ->
                        vm.sendEvent(
                            EditOrderUiEvent.AddNewOrderItem(
                                dishId = dish.id,
                                dishName = dish.name
                            )
                        )
                    }
                )
            }
        },
        topBar = { EditOrderTopAppBar(state = topAppBarState) },
        snackbarHost = { SnackbarHost(it) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(
                        if (scaffoldState.bottomSheetState.targetValue == SheetValue.Expanded
                            && scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded
                        ) 1f
                        else if (scaffoldState.bottomSheetState.targetValue == SheetValue.PartiallyExpanded
                            && scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded
                        ) 1f
                        else if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) 0.5f
                        else 1f
                    )
//                    .padding(horizontal = 16.dp)
                ,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item(key = "s-0") {
                    OrderItemsGroupsDivider(
                        modifier = Modifier
                            .animateItemPlacement()
                            .padding(top = 6.dp, start = 2.dp, end = 2.dp),
                        title = "Сохраненные позиции"
                    )
                }
                sessions.forEach { session ->
                    val allSessionItemsSelected = state.getAllSelectedSavedItems()
                        ?.flatMap { it.dishes }
                        ?.containsAll(session.dishes)
                        ?: false
                    item(key = session.uni) {
                        SessionCard(
                            modifier = Modifier
                                .animateItemPlacement()
                                .padding(horizontal = 8.dp),
                            session = session,
                            select = allSessionItemsSelected,
                            onLongClick = {
                                vm.sendEvent(
                                    if (allSessionItemsSelected) UnselectSavedOrderItems(session)
                                    else SelectSavedItems(session)
                                )
                            }
                        )
                    }
                    items(items = session.dishes, key = { it.getKey(session.uni) }) { orderItem ->
                        if (orderItem.quantity > 0) {
                            val isSelected = state.getAllSelectedSavedItems()
                                ?.find { it.uni == session.uni }
                                ?.dishes
                                ?.any { it.uni == orderItem.uni }
                                ?: false
                            SavedOrderItemCard(
                                modifier = Modifier
                                    .animateItemPlacement()
                                    .padding(horizontal = 16.dp),
                                item = orderItem,
                                select = isSelected,
                                onLongClick = {
                                    val target = session.copy(dishes = listOf(orderItem))
                                    vm.sendEvent(
                                        if (isSelected) UnselectSavedOrderItems(session = target)
                                        else SelectSavedItems(session = target)
                                    )
                                },
                                onClick = {
                                    vm.sendEvent(
                                        EditOrderUiEvent.AddNewOrderItem(
                                            dishId = orderItem.id,
                                            dishName = orderItem.name
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
                item(key = "s-new") {
                    OrderItemsGroupsDivider(
                        modifier = Modifier.animateItemPlacement()
                            .padding(horizontal = 4.dp),
                        title = "Новые позиции"
                    )
                }
                items(items = newOrderItems, key = { it.uuid }) { orderItem ->
                    SwipeToDismissComponent(
                        modifier = Modifier.animateItemPlacement()
                            .padding(horizontal = 16.dp),
                        onStartToEnd = { vm.sendEvent(EditOrderUiEvent.RemoveNewOrderItem(orderItem)) },
                        onEndToStart = { vm.sendEvent(EditOrderUiEvent.RemoveNewOrderItem(orderItem)) }
                    ) {
                        NewOrderItemCard(
                            item = orderItem,
                            enabled = true,
                            select = orderItem.uuid == selectedNewItem?.uuid,
                            onLongClick = {
                                vm.sendEvent(
                                    EditOrderUiEvent.SelectNewOrderItem(
                                        orderItem.uuid
                                    )
                                )
                            },
                            onClick = {
                                vm.sendEvent(
                                    EditOrderUiEvent.IncreaseNewOrderItemQuantity(
                                        orderItem
                                    )
                                )
                            }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(88.dp)) }
            }

            ExpandableFAB(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                items = fabItems
            )

        }
    }

    FullscreenLoader(isShown = state.isSaving || state.isRemoving)

    EditNewOrderItemDialog(
        expanded = isEditNewOrderItemDialogOpen,
        orderItem = selectedNewItem,
        allModifiers = modifiers,
        allDishes = dishes,
        onConfirm = { vm.sendEvent(EditOrderUiEvent.CreateOrUpdateNewOrderItem(it)) },
        onDismiss = { isEditNewOrderItemDialogOpen = false }
    )

    state.interceptedAdding?.let { intercepted ->
        InterceptedAddingDialog(
            warningType = intercepted.warningType,
            onDismiss = { vm.sendEvent(EditOrderUiEvent.RejectInterceptedAdding) },
            onConfirm = { vm.sendEvent(EditOrderUiEvent.ConfirmInterceptedAdding) }
        )
    }

    when(selected) {
        is Selected.SavedItems -> {
            selected.withSingleItem()?.let { item ->
                val targetDish = item.dishes.first()
                RemoveSingleOrderItemDialog(
                    expanded = isSavedItemRemoveDialogOpen,
                    itemName = targetDish.name,
                    initialQuantity = targetDish.quantity,
                    maxQuantity = targetDish.quantity,
                    onConfirm = { vm.sendEvent(EditOrderUiEvent.RemoveSelectedSavedItem(it)) },
                    onDismiss = { isSavedItemRemoveDialogOpen = false }
                )
            }?: RemoveItemsDialog(
                expanded = isSavedItemRemoveDialogOpen,
                count = selected.items.count(),
                onConfirm = { vm.sendEvent(EditOrderUiEvent.RemoveAllSelectedSavedItems) },
                onDismiss = { isSavedItemRemoveDialogOpen = false }
            )
        }
        else -> {}
    }
}

private fun Order.Dish.getKey(sessionUni: String) = "$sessionUni-$uni"