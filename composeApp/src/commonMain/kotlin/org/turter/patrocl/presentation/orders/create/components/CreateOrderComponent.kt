package org.turter.patrocl.presentation.orders.create.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import org.turter.patrocl.presentation.components.FullscreenLoader
import org.turter.patrocl.presentation.components.SwipeToDismissComponent
import org.turter.patrocl.presentation.components.btn.ExpandableFAB
import org.turter.patrocl.presentation.components.btn.FABItem
import org.turter.patrocl.presentation.orders.common.EditNewOrderItemDialog
import org.turter.patrocl.presentation.orders.common.InterceptedAddingDialog
import org.turter.patrocl.presentation.orders.common.MenuSelectorComponent
import org.turter.patrocl.presentation.orders.common.NewOrderItemCard
import org.turter.patrocl.presentation.orders.common.TablePickerDialog
import org.turter.patrocl.presentation.orders.create.CreateOrderScreenState
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.CreateOrUpdateNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.IncreaseNewOrderItemQuantity
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.RemoveNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.SelectNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.SelectTable
import org.turter.patrocl.presentation.orders.create.CreateOrderViewModel
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent
import org.turter.patrocl.ui.icons.Table_restaurant

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CreateOrderComponent(
    vm: CreateOrderViewModel,
    state: CreateOrderScreenState.Main,
    paddingValues: PaddingValues = PaddingValues()
) {
    val newOrderItems = state.newOrderItems
    val selectedItemUuid = state.selectedNewItemUuid
    val isItemSelect = selectedItemUuid != null
    val selectedItem = state.getSelectedItem()
    val category = state.menuData.rootCategory
    val dishes = state.menuData.dishes
    val modifiers = state.menuData.modifiers
    val tables = state.tables
    val selectedTable = state.selectedTable
    val isTablePickerOpened = state.isTablePickerOpen
    val isSaving = state.isSaving

    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()

    var isEditNewOrderItemDialogOpen by remember { mutableStateOf(false) }

    val fabItems = listOf(
        FABItem(
            icon = Table_restaurant,
            text = "Стол",
            action = { vm.sendEvent(CreateOrderUiEvent.OpenTablePicker) }
        ),
        FABItem(
            icon = Icons.Default.Check,
            text = "Применить",
            action = { vm.sendEvent(CreateOrderUiEvent.CreateAndOpenOrder) }
        ),
        FABItem(
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            text = "Создать",
            action = { vm.sendEvent(CreateOrderUiEvent.CreateOrderAndGoToOrders) }
        )
    )

    BottomSheetScaffold(
        modifier = Modifier.padding(paddingValues),
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
                            CreateOrderUiEvent.AddNewOrderItem(
                                dishId = dish.id,
                                dishName = dish.name
                            )
                        )
                    }
                )
            }
        },
        topBar = {
            CreateOrderTopAppBar(
                showSelectedItemBar = isItemSelect,
                selectedItem = selectedItem,
                selectedTable = selectedTable,
                onBack = { vm.sendEvent(CreateOrderUiEvent.BackToOrders) },
                onTableOpen = { vm.sendEvent(CreateOrderUiEvent.OpenTablePicker) },
                onMenuOpen = { coroutineScope.launch { scaffoldState.bottomSheetState.expand() } },
                onClose = { vm.sendEvent(CreateOrderUiEvent.UnselectNewOrderItem) },
                onMoveUp = { vm.sendEvent(CreateOrderUiEvent.MoveSelectedItemUp) },
                onMoveDown = { vm.sendEvent(CreateOrderUiEvent.MoveSelectedItemDown) },
                onInfo = { isEditNewOrderItemDialogOpen = true }
            )
        },
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
                    .padding(horizontal = 16.dp)
            ) {
                items(items = newOrderItems, key = { it.uuid }) { orderItem ->
                    SwipeToDismissComponent(
                        modifier = Modifier.padding(top = 6.dp).animateItemPlacement(),
                        onStartToEnd = { vm.sendEvent(RemoveNewOrderItem(orderItem)) },
                        onEndToStart = { vm.sendEvent(RemoveNewOrderItem(orderItem)) }
                    ) {
                        NewOrderItemCard(
                            item = orderItem,
                            enabled = true,
                            select = orderItem.uuid == selectedItem?.uuid,
                            onLongClick = { vm.sendEvent(SelectNewOrderItem(orderItem.uuid)) },
                            onClick = { vm.sendEvent(IncreaseNewOrderItemQuantity(orderItem)) }
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(88.dp))
                }
            }

            ExpandableFAB(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                items = fabItems
            )

        }
    }

    FullscreenLoader(isShown = isSaving)

    TablePickerDialog(
        isOpened = isTablePickerOpened,
        tables = tables,
        selectedTable = selectedTable,
        onDismiss = { vm.sendEvent(CreateOrderUiEvent.CloseTablePicker) },
        onSelectTable = { vm.sendEvent(SelectTable(it)) }
    )

    EditNewOrderItemDialog(
        expanded = isEditNewOrderItemDialogOpen,
        orderItem = selectedItem,
        allModifiers = modifiers,
        allDishes = dishes,
        onConfirm = { vm.sendEvent(CreateOrUpdateNewOrderItem(it)) },
        onDismiss = { isEditNewOrderItemDialogOpen = false }
    )

    state.interceptedAdding?.let { intercepted ->
        InterceptedAddingDialog(
            warningType = intercepted.warningType,
            onDismiss = { vm.sendEvent(CreateOrderUiEvent.RejectInterceptedAdding) },
            onConfirm = { vm.sendEvent(CreateOrderUiEvent.ConfirmInterceptedAdding) }
        )
    }
}