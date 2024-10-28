package org.turter.patrocl.presentation.orders.create

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
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.domain.service.MenuService
import org.turter.patrocl.domain.service.OrderService
import org.turter.patrocl.domain.service.TableService
import org.turter.patrocl.domain.service.WaiterService
import org.turter.patrocl.presentation.error.ErrorType
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.AddNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.CloseNewOrderItemDialog
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.CreateOrUpdateNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.CreateOrder
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.IncreaseNewOrderItemQuantity
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.OpenCreateNewOrderItemDialog
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.OpenEditNewOrderItemDialog
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.RefreshData
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.RemoveNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.SelectTable

sealed class CreateOrderUiEvent {
    data class SelectTable(val table: Table) : CreateOrderUiEvent()
    data class AddNewOrderItem(
        val dishId: String,
        val dishName: String,
        val quantity: Float = 1f,
        val modifiers: List<ItemModifier> = emptyList()
    ) : CreateOrderUiEvent()

    data class AddNewOrderItems(val items: List<NewOrderItem>) : CreateOrderUiEvent()
    data class RemoveNewOrderItem(val item: NewOrderItem) : CreateOrderUiEvent()
    data class IncreaseNewOrderItemQuantity(val item: NewOrderItem) : CreateOrderUiEvent()
    data class CreateOrUpdateNewOrderItem(val item: NewOrderItem) : CreateOrderUiEvent()
    data class OpenEditNewOrderItemDialog(val item: NewOrderItem) : CreateOrderUiEvent()
    data object OpenCreateNewOrderItemDialog : CreateOrderUiEvent()
    data object CloseNewOrderItemDialog : CreateOrderUiEvent()
    data object RefreshData : CreateOrderUiEvent()
    data object CreateOrder : CreateOrderUiEvent()
}

class CreateOrderViewModel(
    private val menuService: MenuService,
    private val tableService: TableService,
    private val waiterService: WaiterService,
    private val orderService: OrderService
) : ScreenModel {
    private val log = Logger.withTag("CreateOrderViewModel")

    private val coroutineScope = screenModelScope

    private val _screenState =
        MutableStateFlow<CreateOrderScreenState>(CreateOrderScreenState.Initial)

    val screenState = _screenState.asStateFlow()

    init {
        coroutineScope.launch {
            combine(
                menuService.getMenuDataStateFlow(),
                tableService.getTablesStateFlow(),
                waiterService.getOwnWaiterStateFlow()
            ) { menu, tables, waiter ->
                log.d {
                    "Combine flows:\n " +
                            "-Menu: $menu \n" +
                            "-Tables: $tables \n" +
                            "-Waiter: $waiter"
                }
                if (menu is Finished && tables is Finished && waiter is Finished) {
                    try {
                        CreateOrderScreenState.CreateNewOrder(
                            menuData = menu.result.getOrThrow(),
                            tables = tables.result.getOrThrow(),
                            ownWaiter = waiter.result.getOrThrow(),
                            newOrderItems = emptyList()
                        )
                    } catch (e: Exception) {
                        log.e { "Catch exception in combine flows: $e" }
                        e.printStackTrace()
                        CreateOrderScreenState.Error(errorType = ErrorType.from(e))
                    }
                } else {
                    CreateOrderScreenState.Loading
                }
            }.collect { newScreenState: CreateOrderScreenState ->
                _screenState.value = newScreenState
            }
        }
    }

    fun sendEvent(event: CreateOrderUiEvent) {
        when (event) {
            is SelectTable -> selectTable(event.table)
            is AddNewOrderItem -> addNewOrderItem(
                dishId = event.dishId,
                dishName = event.dishName,
                quantity = event.quantity,
                modifiers = event.modifiers
            )

            is CreateOrderUiEvent.AddNewOrderItems -> addOrderItems(event.items)
            is RemoveNewOrderItem -> removeNewOrderItem(event.item)
            is IncreaseNewOrderItemQuantity -> increaseQuantity(event.item)
            is CreateOrUpdateNewOrderItem -> createOrUpdateOrderItem(event.item)
            is OpenEditNewOrderItemDialog -> openEditDialog(event.item)
            is OpenCreateNewOrderItemDialog -> openCreateDialog()
            is CloseNewOrderItemDialog -> closeEditDialog()
            is RefreshData -> refreshData()
            is CreateOrder -> createOrder()
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

    //TODO в случае неудачи сохранять заказ в буфер, дабы потом его открыть
    private fun createOrder() = exactInContent {
        coroutineScope.launch {
            log.d { "On create order" }
            selectedTable?.let { table ->
                orderService.createOrder(
                    table = table,
                    waiter = ownWaiter,
                    orderItems = newOrderItems
                )
                _screenState.value = CreateOrderScreenState.RedirectToOrders
            }
        }
    }

    private fun refreshData() {
        _screenState.value = CreateOrderScreenState.Loading
        coroutineScope.launch {
            menuService.refreshMenu()
            tableService.refreshTables()
        }
    }

    private fun exactInContent(action: CreateOrderScreenState.CreateNewOrder.() -> Unit) {
        val currentState = _screenState.value
        if (currentState is CreateOrderScreenState.CreateNewOrder) {
            _screenState.value = currentState.copy().apply(action)
        }
    }

}