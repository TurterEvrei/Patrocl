package org.turter.patrocl.presentation.orders.edit

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.FetchState.Finished
import org.turter.patrocl.domain.model.order.ItemModifier
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.SavedOrderItem
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.domain.service.MenuService
import org.turter.patrocl.domain.service.OrderService
import org.turter.patrocl.domain.service.TableService
import org.turter.patrocl.domain.service.WaiterService
import org.turter.patrocl.presentation.error.ErrorType
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.AddNewOrderItem
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.CloseNewOrderItemDialog
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.CloseRemovingSavedOrderItemDialog
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.ConfirmRemovingSavedOrderItem
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.IncreaseNewOrderItemQuantity
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.RemoveNewOrderItem
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.SelectTable
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.CreateOrUpdateNewOrderItem
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.OpenCreateNewOrderItemDialog
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.OpenEditNewOrderItemDialog
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.OpenRemovingSavedOrderItemDialog
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.RefreshData
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.SaveOrder

sealed class EditOrderUiEvent {
    data class SelectTable(val table: Table): EditOrderUiEvent()
    data class AddNewOrderItem(
        val dishId: String,
        val dishName: String,
        val quantity: Float = 1f,
        val modifiers: List<ItemModifier> = emptyList()
    ): EditOrderUiEvent()
    data class AddNewOrderItems(val items: List<NewOrderItem>): EditOrderUiEvent()
    data class RemoveNewOrderItem(val item: NewOrderItem): EditOrderUiEvent()
    data class IncreaseNewOrderItemQuantity(val item: NewOrderItem): EditOrderUiEvent()
    data class CreateOrUpdateNewOrderItem(val item: NewOrderItem): EditOrderUiEvent()
    data class ConfirmRemovingSavedOrderItem(
        val orderItem: SavedOrderItem,
        val quantity: Float
    ): EditOrderUiEvent()
    data class OpenEditNewOrderItemDialog(val item: NewOrderItem): EditOrderUiEvent()
    data object OpenCreateNewOrderItemDialog: EditOrderUiEvent()
    data object CloseNewOrderItemDialog: EditOrderUiEvent()
    data class OpenRemovingSavedOrderItemDialog(val item: SavedOrderItem): EditOrderUiEvent()
    data object CloseRemovingSavedOrderItemDialog: EditOrderUiEvent()
    data object RefreshData: EditOrderUiEvent()
    data object SaveOrder: EditOrderUiEvent()
}

class EditOrderViewModel(
    private val orderGuid: String,
    private val menuService: MenuService,
    private val tableService: TableService,
    private val waiterService: WaiterService,
    private val orderService: OrderService
) : ScreenModel {
    private val log = Logger.withTag("EditOrderViewModel")

    private val coroutineScope = screenModelScope

    private val _screenState = MutableStateFlow<EditOrderScreenState>(EditOrderScreenState.Initial)

    val screenState = _screenState.asStateFlow()

    init {
        coroutineScope.launch {
            combine(
                orderService.getOrderFlow(orderGuid),
                menuService.getMenuDataStateFlow(),
                tableService.getTablesStateFlow(),
                waiterService.getOwnWaiterStateFlow()
            ) { order, menu, tables, waiter ->
                log.d {
                    "Combine flows:\n " +
                            "-Order: $order \n" +
                            "-Menu: $menu \n" +
                            "-Tables: $tables \n" +
                            "-Waiter: $waiter"
                }
                if (order is Finished && menu is Finished && tables is Finished && waiter is Finished) {
                    try {
                        val orderData = order.result.getOrThrow()
                        val tablesData = tables.result.getOrThrow()
                        EditOrderScreenState.UpdateSavedOrder(
                            order = orderData,
                            menuData = menu.result.getOrThrow(),
                            tables = tablesData,
                            ownWaiter = waiter.result.getOrThrow(),
                            newOrderItems = emptyList(),
                            selectedTable = tablesData.first { it.id == orderData.table.id }
                        )
                    } catch (e: Exception) {
                        log.e { "Catch exception in combine flows: $e" }
                        e.printStackTrace()
                        EditOrderScreenState.Error(errorType = ErrorType.from(e))
                    }
                } else {
                    EditOrderScreenState.Loading
                }
            }.collect { newScreenState: EditOrderScreenState ->
                _screenState.value = newScreenState
            }
        }
    }

    fun sendEvent(event: EditOrderUiEvent) {
        when (event) {
            is SelectTable -> selectTable(event.table)
            is AddNewOrderItem -> addNewOrderItem(
                dishId = event.dishId,
                dishName = event.dishName,
                quantity = event.quantity,
                modifiers = event.modifiers
            )
            is EditOrderUiEvent.AddNewOrderItems -> addOrderItems(event.items)
            is RemoveNewOrderItem -> removeNewOrderItem(event.item)
            is IncreaseNewOrderItemQuantity -> increaseQuantity(event.item)
            is CreateOrUpdateNewOrderItem -> createOrUpdateOrderItem(event.item)
            is ConfirmRemovingSavedOrderItem -> confirmRemove(
                orderItem = event.orderItem,
                quantity = event.quantity
            )
            is OpenEditNewOrderItemDialog -> openEditDialog(event.item)
            is OpenCreateNewOrderItemDialog -> openCreateDialog()
            is CloseNewOrderItemDialog -> closeEditDialog()
            is OpenRemovingSavedOrderItemDialog -> openRemovingSavedOrderItemDialog(event.item)
            is CloseRemovingSavedOrderItemDialog -> closeConfirmRemovingDialog()
            is RefreshData -> refreshData()
            is SaveOrder -> saveOrder()
        }
    }

    private fun selectTable(table: Table) {
        exactInContent { selectedTable = table }
    }

    private fun addNewOrderItem(
        dishId: String,
        dishName: String,
        quantity: Float = 1f,
        modifiers: List<ItemModifier> = emptyList()
    ) {
        exactInContent {
            newOrderItems = newOrderItems.map { it.copy() }.toMutableList().apply {
                add(
                    NewOrderItem(
                        dishId = dishId,
                        dishName = dishName,
                        quantity = quantity,
                        modifiers = modifiers
                    )
                )
            }.toList()
        }
    }

    private fun addOrderItems(data: List<NewOrderItem>) = exactInContent {
        newOrderItems =
            newOrderItems.map { it.copy() }.toMutableList().apply { addAll(data) }.toList()
    }

    private fun removeNewOrderItem(orderItem: NewOrderItem) {
        exactInContent {
            newOrderItems = newOrderItems.filter { item -> item.uuid != orderItem.uuid }
        }
    }

    private fun increaseQuantity(orderItem: NewOrderItem) = exactInContent {
        newOrderItems = newOrderItems.map {
            if (it.uuid == orderItem.uuid) it.copy(quantity = it.quantity + 1) else it
        }.toList()
    }

    private fun createOrUpdateOrderItem(orderItem: NewOrderItem) = exactInContent {
        newOrderItems = newOrderItems.map { it.copy() }.toMutableList().apply {
            val filtered = filter { it.uuid == orderItem.uuid }
            if (filtered.isNotEmpty()) this[indexOf(filtered.first())] = orderItem
            else add(orderItem)
        }.toList()
    }

    private fun confirmRemove(orderItem: SavedOrderItem, quantity: Float) = exactInContent {
        coroutineScope.launch {
            orderService.removeItemFromOrder(
                orderGuid = orderGuid, orderItem = orderItem, quantity = quantity
            ).apply {
//                if (this.isSuccess) {
//                    orderItem.quantity -= quantity
//                    orderItems = orderItems.map { it.copy() }
//                        .toMutableList()
//                        .apply {
//                            val filtered = filter { it.uuid == orderItem.uuid }
//                            if (filtered.isNotEmpty()) this[indexOf(filtered.first())] = orderItem
//                        }
//                        .toList()
//                }
            }
        }
    }

    private fun openEditDialog(orderItem: NewOrderItem) = exactInContent {
        newOrderItemForDialog = orderItem
        expandedOrderItemDialog = true
    }

    //TODO использовать при longclick на элементе меню
    private fun openCreateDialog() = exactInContent {
        newOrderItemForDialog = null
        expandedOrderItemDialog = true
    }

    private fun closeEditDialog() = exactInContent { expandedOrderItemDialog = false }

    private fun openRemovingSavedOrderItemDialog(orderItem: SavedOrderItem) {
        exactInContent {
            orderItemForRemoving = orderItem
            expandedConfirmRemovingOrderItem = true
        }
    }

    private fun closeConfirmRemovingDialog() = exactInContent { expandedConfirmRemovingOrderItem = false }

    //TODO в случае неудачи сохранять заказ в буфер, дабы потом его открыть
    private fun saveOrder() = exactInContent {
        coroutineScope.launch {
            orderService.updateOrder(
                orderGuid = order.guid,
                waiterCode = ownWaiter.code,
                tableCode = selectedTable.code,
                orderItems = newOrderItems
            ).let { _screenState.value = EditOrderScreenState.RedirectToOrders }
        }
    }

    private fun refreshData() {
        _screenState.value = EditOrderScreenState.Loading
        coroutineScope.launch {
            tableService.refreshTablesFromApi()
        }
        coroutineScope.launch {
            menuService.refreshMenuFromApi()
        }
    }

    private fun exactInContent(action: EditOrderScreenState.UpdateSavedOrder.() -> Unit) {
        val currentState = _screenState.value
        if (currentState is EditOrderScreenState.UpdateSavedOrder) {
            _screenState.value = currentState.copy().apply(action)
        }
    }
}