package org.turter.patrocl.presentation.orders.create

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import com.benasher44.uuid.Uuid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.FetchState.Finished
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.Order
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.domain.service.MenuService
import org.turter.patrocl.domain.service.OrderService
import org.turter.patrocl.domain.service.TableService
import org.turter.patrocl.domain.service.WaiterService
import org.turter.patrocl.presentation.error.ErrorType
import org.turter.patrocl.presentation.orders.common.AddingWarningType
import org.turter.patrocl.presentation.orders.common.InterceptedAddingDish
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.AddNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.BackToOrders
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.CloseTablePicker
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.ConfirmInterceptedAdding
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.CreateAndOpenOrder
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.CreateOrUpdateNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.CreateOrderAndGoToOrders
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.IncreaseNewOrderItemQuantity
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.MoveSelectedItemDown
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.MoveSelectedItemUp
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.OpenTablePicker
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.RefreshData
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.RejectInterceptedAdding
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.RemoveNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.SelectNewOrderItem
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.SelectTable
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.UnselectNewOrderItem
import org.turter.patrocl.presentation.orders.edit.EditOrderScreen

sealed class CreateOrderUiEvent {
    data object BackToOrders : CreateOrderUiEvent()

    data class SelectTable(val table: Table) : CreateOrderUiEvent()
    data object OpenTablePicker : CreateOrderUiEvent()
    data object CloseTablePicker : CreateOrderUiEvent()

    data class AddNewOrderItem(
        val dishId: String,
        val dishName: String,
        val quantity: Float = 1f,
        val modifiers: List<NewOrderItem.Modifier> = emptyList()
    ) : CreateOrderUiEvent()
    data object ConfirmInterceptedAdding : CreateOrderUiEvent()
    data object RejectInterceptedAdding : CreateOrderUiEvent()
    
//    data class AddNewOrderItems(val items: List<NewOrderItem>) : CreateOrderUiEvent()
    data class RemoveNewOrderItem(val item: NewOrderItem) : CreateOrderUiEvent()
    data class IncreaseNewOrderItemQuantity(val item: NewOrderItem) : CreateOrderUiEvent()
    data class CreateOrUpdateNewOrderItem(val item: NewOrderItem) : CreateOrderUiEvent()

    data class SelectNewOrderItem(val uuid: Uuid) : CreateOrderUiEvent()
    data object UnselectNewOrderItem : CreateOrderUiEvent()

    data object MoveSelectedItemUp : CreateOrderUiEvent()
    data object MoveSelectedItemDown : CreateOrderUiEvent()

    data object RefreshData : CreateOrderUiEvent()
    data object CreateOrderAndGoToOrders : CreateOrderUiEvent()
    data object CreateAndOpenOrder : CreateOrderUiEvent()
}

class CreateOrderViewModel(
    private val menuService: MenuService,
    private val tableService: TableService,
    private val waiterService: WaiterService,
    private val orderService: OrderService,
    private val navigator: Navigator
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
                        val res = CreateOrderScreenState.Main(
                            menuData = menu.result.getOrThrow(),
                            tables = tables.result.getOrThrow(),
                            ownWaiter = waiter.result.getOrThrow(),
                        )
                        res
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
            is BackToOrders -> navigator.popUntilRoot()
            is SelectTable -> selectTable(event.table)
            is OpenTablePicker -> openTablePicker()
            is CloseTablePicker -> closeTablePicker()

            is AddNewOrderItem -> addNewOrderItem(
                dishId = event.dishId,
                dishName = event.dishName,
                quantity = event.quantity,
                modifiers = event.modifiers
            )
            is ConfirmInterceptedAdding -> confirmInterceptedAdding()
            is RejectInterceptedAdding -> cleanInterceptedAdding()

            is RemoveNewOrderItem -> removeNewOrderItem(event.item)
            is IncreaseNewOrderItemQuantity -> increaseQuantity(event.item)
            is CreateOrUpdateNewOrderItem -> createOrUpdateOrderItem(event.item)
            is SelectNewOrderItem -> selectNewItem(event.uuid)
            is UnselectNewOrderItem -> unselectNewItem()
            is MoveSelectedItemUp -> moveSelectedItemUp()
            is MoveSelectedItemDown -> moveSelectedItemDown()
            is RefreshData -> refreshData()
            is CreateOrderAndGoToOrders -> createOrderAndOpenOrdersList()
            is CreateAndOpenOrder -> createAndOpenOrder()
        }
    }

    private fun selectTable(table: Table) = transformMainState { it.copy(selectedTable = table) }

    private fun openTablePicker() = transformMainState {
        log.d { "Open table picker" }
        it.copy(isTablePickerOpen = true)
    }

    private fun closeTablePicker() = transformMainState {
        log.d { "Close table picker" }
        it.copy(isTablePickerOpen = false)
    }

    private fun addNewOrderItem(
        dishId: String,
        dishName: String,
        quantity: Float = 1f,
        modifiers: List<NewOrderItem.Modifier>
    ) {
        withMainState()?.apply {
            val target = NewOrderItem(
                dishId = dishId,
                dishName = dishName,
                quantity = quantity,
                modifiers = modifiers
            )
            menuData.dishes.find { it.id == dishId }
                ?.takeIf { it.onStop || it.remainingCount - quantity < 5 }
                ?.let { dish ->
                    transformMainState {
                        it.copy(
                            interceptedAdding = InterceptedAddingDish(
                                target = target,
                                warningType = AddingWarningType.of(dish.onStop, dish.remainingCount)
                            )
                        )
                    }
                }
                ?: newOrderItems.add(target)
        }
    }

    private fun confirmInterceptedAdding() {
        withMainState()?.apply {
            interceptedAdding?.let { intercepted ->
                newOrderItems.add(intercepted.target)
                cleanInterceptedAdding()
            }
        }
    }

    private fun cleanInterceptedAdding() = transformMainState { it.copy(interceptedAdding = null) }

    private fun removeNewOrderItem(orderItem: NewOrderItem) =
        withMainState()?.newOrderItems?.remove(orderItem)

    private fun increaseQuantity(orderItem: NewOrderItem) =
        withMainState()?.newOrderItems?.let {
            val index = it.indexOf(orderItem)
            val item = orderItem.copy(quantity = orderItem.quantity.inc())
            it[index] = item
            log.d { "Index of item to increase: $index, item: $orderItem" }
        }

    private fun createOrUpdateOrderItem(orderItem: NewOrderItem) =
        withMainState()?.newOrderItems?.let {
            val item = it.find { it.uuid == orderItem.uuid }
            if (item != null) {
                it[it.indexOf(item)] = orderItem
            } else {
                it.add(orderItem)
            }
        }

    private fun selectNewItem(uuid: Uuid) = transformMainState {
        it.copy(selectedNewItemUuid = uuid)
    }

    private fun unselectNewItem() = transformMainState {
        it.copy(selectedNewItemUuid = null)
    }

    private fun moveSelectedItemUp() = withMainState()?.let { state ->
        state.getSelectedItem()?.let { item ->
            val items = state.newOrderItems
            val index = items.indexOf(item)
            if (index > 0) {
                val targetIndex = index - 1
                val temp = items[targetIndex]
                items[targetIndex] = item
                items[index] = temp
            }
        }
    }

    private fun moveSelectedItemDown() = withMainState()?.let { state ->
        state.getSelectedItem()?.let { item ->
            val items = state.newOrderItems
            val index = items.indexOf(item)
            if (index < items.size - 1) {
                val targetIndex = index + 1
                val temp = items[targetIndex]
                items[targetIndex] = item
                items[index] = temp
            }
        }
    }

    private fun createOrderAndOpenOrdersList() {
        log.d { "On create order and go to all orders" }
        createOrder { navigator.popUntilRoot() }
    }

    private fun createAndOpenOrder() {
        log.d { "On create order and go to created order" }
        createOrder { navigator.replace(EditOrderScreen(orderGuid = it.guid)) }
    }

    private fun createOrder(onSuccess: (Order) -> Unit) {
        withMainState()?.let { state ->
            when (val table = state.selectedTable) {
                null -> openTablePicker()
                else -> coroutineScope.launch {
                    setLoading(true)
                    orderService.createOrder(
                        table = table,
                        waiter = state.ownWaiter,
                        orderItems = state.newOrderItems
                    ).onSuccess(onSuccess)
                    setLoading(false)
                }
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

    private fun setLoading(value: Boolean) {
        log.d { "Set isSaving to $value" }
        transformMainState { it.copy(isSaving = value) }
    }

    private fun transformMainState(
        action: (state: CreateOrderScreenState.Main) -> CreateOrderScreenState
    ) {
        val currentState = _screenState.value
        if (currentState is CreateOrderScreenState.Main) {
            _screenState.value = action(currentState)
        }
    }

    private fun withMainState(): CreateOrderScreenState.Main? {
        val state = _screenState.value
        return if (state is CreateOrderScreenState.Main) state else null
    }
}

