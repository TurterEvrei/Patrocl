package org.turter.patrocl.presentation.orders.edit

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
import org.turter.patrocl.domain.service.MenuService
import org.turter.patrocl.domain.service.OrderService
import org.turter.patrocl.domain.service.TableService
import org.turter.patrocl.domain.service.WaiterService
import org.turter.patrocl.presentation.error.ErrorType
import org.turter.patrocl.presentation.orders.common.AddingWarningType
import org.turter.patrocl.presentation.orders.common.InterceptedAddingDish
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.AddNewOrderItem
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.BackToOrders
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.ConfirmInterceptedAdding
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.CreateOrUpdateNewOrderItem
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.IncreaseNewOrderItemQuantity
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.MoveSelectedItemDown
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.MoveSelectedItemUp
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.RefreshData
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.RejectInterceptedAdding
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.RemoveNewOrderItem
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.RemoveAllSelectedSavedItems
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.RemoveSelectedSavedItem
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.SaveOrderAndContinue
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.SaveOrderAndGoToOrders
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.SelectNewOrderItem
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.SelectSavedItems
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.UnselectAllItems
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.UnselectSavedOrderItems

sealed class EditOrderUiEvent {
    data object BackToOrders : EditOrderUiEvent()

    data class AddNewOrderItem(
        val dishId: String,
        val dishName: String,
        val quantity: Float = 1f,
        val modifiers: List<NewOrderItem.Modifier> = emptyList()
    ) : EditOrderUiEvent()
    data object ConfirmInterceptedAdding : EditOrderUiEvent()
    data object RejectInterceptedAdding : EditOrderUiEvent()

    data class RemoveNewOrderItem(val item: NewOrderItem) : EditOrderUiEvent()
    data class IncreaseNewOrderItemQuantity(val item: NewOrderItem) : EditOrderUiEvent()
    data class CreateOrUpdateNewOrderItem(val item: NewOrderItem) : EditOrderUiEvent()

    data class SelectSavedItems(val session: Order.Session) : EditOrderUiEvent()
    data class UnselectSavedOrderItems(val session: Order.Session) : EditOrderUiEvent()
    data class RemoveSelectedSavedItem(val quantity: Float) : EditOrderUiEvent()
    data object RemoveAllSelectedSavedItems : EditOrderUiEvent()

    data class SelectNewOrderItem(val uuid: Uuid) : EditOrderUiEvent()
    data object UnselectAllItems : EditOrderUiEvent()

    data object MoveSelectedItemUp : EditOrderUiEvent()
    data object MoveSelectedItemDown : EditOrderUiEvent()

    data object RefreshData : EditOrderUiEvent()
    data object SaveOrderAndGoToOrders : EditOrderUiEvent()
    data object SaveOrderAndContinue : EditOrderUiEvent()
}

class EditOrderViewModel(
    private val orderGuid: String,
    private val menuService: MenuService,
    private val tableService: TableService,
    private val waiterService: WaiterService,
    private val orderService: OrderService,
    private val navigator: Navigator
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
//                tableService.getTablesStateFlow(),
                waiterService.getOwnWaiterStateFlow()
            ) { order, menu, waiter ->
                log.d {
                    "Combine flows:\n " +
                            "-Order: $order \n" +
                            "-Menu: $menu \n" +
                            "-Waiter: $waiter"
                }
                if (order is Finished && menu is Finished && waiter is Finished) {
                    try {
                        val orderData = order.result.getOrThrow()
//                        val tablesData = tables.result.getOrThrow()
                        EditOrderScreenState.Main(
                            order = orderData,
                            menuData = menu.result.getOrThrow(),
//                            tables = tablesData,
                            ownWaiter = waiter.result.getOrThrow()
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
            is BackToOrders -> navigator.popUntilRoot()
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
            is SelectSavedItems -> selectSavedOrderItems(event.session)
            is UnselectSavedOrderItems -> unselectSavedOrderItems(event.session)
            is RemoveSelectedSavedItem -> removeSelectedSavedItem(event.quantity)
            is RemoveAllSelectedSavedItems -> removeAllSelectedSavedItems()
            is SelectNewOrderItem -> selectNewItem(event.uuid)
            is UnselectAllItems -> unselectAllItems()
            is MoveSelectedItemUp -> moveSelectedItemUp()
            is MoveSelectedItemDown -> moveSelectedItemDown()
            is RefreshData -> refreshData()
            is SaveOrderAndGoToOrders -> saveOrderAndOpenOrdersList()
            is SaveOrderAndContinue -> saveNewOrderItems()
        }
    }

    private fun addNewOrderItem(
        dishId: String,
        dishName: String,
        quantity: Float = 1f,
        modifiers: List<NewOrderItem.Modifier> = emptyList()
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

    private fun removeNewOrderItem(orderItem: NewOrderItem) {
        withMainState()?.apply {
            if (selected is Selected.NewItem && selected.newItemUuid == orderItem.uuid)
                unselectAllItems()
            newOrderItems.remove(orderItem)
        }
    }

    private fun increaseQuantity(orderItem: NewOrderItem) =
        withMainState()?.newOrderItems?.let {
            val index = it.indexOf(orderItem)
            val item = orderItem.copy(quantity = orderItem.quantity.inc())
            it[index] = item
            log.d { "Index of item to increase: $index, item: $orderItem" }
        }

    //TODO при изменении блюда не учитывается стоп-лист
    //TODO необходимо вынести логику изменения NewOrderItem в отдельный экран
    private fun createOrUpdateOrderItem(orderItem: NewOrderItem) =
        withMainState()?.newOrderItems?.let {
            val oldItem = it.find { it.uuid == orderItem.uuid }
            if (oldItem != null) {
                it[it.indexOf(oldItem)] = orderItem
            } else {
                it.add(orderItem)
            }
        }

    private fun selectNewItem(uuid: Uuid) = transformMainState {
        it.copy(selected = Selected.NewItem(newItemUuid = uuid))
    }

    private fun unselectAllItems() = transformMainState {
        it.copy(selected = Selected.None)
    }

    private fun moveSelectedItemUp() = withMainState()?.let { state ->
        state.getSelectedNewItem()?.let { item ->
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
        state.getSelectedNewItem()?.let { item ->
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

    private fun selectSavedOrderItems(target: Order.Session) {
        withMainState()
            ?.getAllSelectedSavedItems()
            ?.addToSelectedOrderItems(target)
            ?: transformMainState {
                val selected = Selected.SavedItems()
                selected.items.addToSelectedOrderItems(target)
                it.copy(selected = selected)
            }
    }

    private fun MutableList<Order.Session>.addToSelectedOrderItems(target: Order.Session) {
        find { it.sessionId == target.sessionId }?.let { session ->
            set(
                indexOf(session),
                session.copy(
                    dishes = target.dishes
                        .filter { dish -> session.dishes.none { it.uni == dish.uni } }
                        .toMutableList()
                        .apply { addAll(session.dishes) }
                        .toList()
                )
            )
        } ?: add(target)
    }

    private fun unselectSavedOrderItems(target: Order.Session) {
        withMainState()?.getAllSelectedSavedItems()?.apply {
            find { it.sessionId == target.sessionId }?.let { session ->
                session.dishes
                    .filter { dish -> target.dishes.none { dish.uni == it.uni } }
                    .toList()
                    .let { list ->
                        if (list.isEmpty()) remove(session)
                        else set(indexOf(session), session.copy(dishes = list))
                    }
            }
            if (flatMap { it.dishes }.isEmpty()) unselectAllItems()
        }
    }

    private fun removeSelectedSavedItem(quantity: Float) {
        withMainState()?.getSingleSelectedSavedItem()?.let { item ->
            val targetDish = item.dishes.first()
            if (targetDish.quantity >= quantity) coroutineScope.launch {
                setRemoving(true)
                orderService.removeItemFromOrderSession(
                    orderGuid = orderGuid,
                    payload = item.copy(
                        dishes = listOf(targetDish.copy(quantity = targetDish.quantity - quantity))
                    )
                ).onSuccess { unselectAllItems() }
                setRemoving(false)
            }
        }
    }

    private fun removeAllSelectedSavedItems() =
        withMainState()?.getAllSelectedSavedItems()?.let { items ->
            if (items.isNotEmpty()) coroutineScope.launch {
                setRemoving(true)
                orderService.removeItemsFromOrderSessions(
                    orderGuid = orderGuid,
                    payload = items
                ).onSuccess { unselectAllItems() }
                setRemoving(false)
            }
        }

    private fun saveOrderAndOpenOrdersList() {
        log.d { "On save order and go to all orders" }
        addNewItemsToOrder { navigator.popUntilRoot() }
    }

    private fun saveNewOrderItems() {
        log.d { "On save order and continue" }
        addNewItemsToOrder()
    }

    private fun addNewItemsToOrder(onSuccess: () -> Unit = {}) {
        withMainState()?.let { state ->
            if (state.newOrderItems.isNotEmpty()) coroutineScope.launch {
                setLoading(true)
                orderService.addItemsToOrder(orderGuid = orderGuid, newItems = state.newOrderItems)
                    .onSuccess {
                        onSuccess()
                        state.newOrderItems.clear()
                    }
                setLoading(false)
            }
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

    private fun setLoading(value: Boolean) {
        log.d { "Set isSaving to $value" }
        transformMainState { it.copy(isSaving = value) }
    }

    private fun setRemoving(value: Boolean) {
        log.d { "Set isRemoving to $value" }
        transformMainState { it.copy(isRemoving = value) }
    }

    private fun transformMainState(
        action: (state: EditOrderScreenState.Main) -> EditOrderScreenState
    ) {
        val currentState = _screenState.value
        if (currentState is EditOrderScreenState.Main) {
            _screenState.value = action(currentState)
        }
    }

    private fun withMainState(): EditOrderScreenState.Main? {
        val state = _screenState.value
        return if (state is EditOrderScreenState.Main) state else null
    }
}