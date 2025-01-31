package org.turter.patrocl.presentation.orders.list

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.service.OrderService
import org.turter.patrocl.domain.service.WaiterService
import org.turter.patrocl.presentation.error.ErrorType

sealed class OrdersUiEvent {
    data object RefreshOrders : OrdersUiEvent()
}

class OrdersViewModel(
    private val orderService: OrderService,
    private val waiterService: WaiterService
) : ScreenModel {

    private val coroutineScope = screenModelScope

    private val _screenState = MutableStateFlow<OrdersScreenState>(OrdersScreenState.Initial)

    val screenState: StateFlow<OrdersScreenState> = _screenState

    init {
        coroutineScope.launch {
            combine(
                orderService.getActiveOrdersStateFlow(),
                waiterService.getOwnWaiterStateFlow()
            ) { fetchedOrders, fetchedWaiter ->
                if (fetchedOrders is FetchState.Finished && fetchedWaiter is FetchState.Finished) {
                    try {
                        OrdersScreenState.Content(
                            orders = fetchedOrders.result.getOrThrow().sortedBy { it.name },
                            waiter = fetchedWaiter.result.getOrThrow()
                        )
                    } catch (e: Exception) {
                        OrdersScreenState.Error(errorType = ErrorType.from(e))
                    }
                } else {
                    OrdersScreenState.Loading
                }
            }.collect { newState ->
                _screenState.value = newState
            }
        }
    }

    fun sendEvent(event: OrdersUiEvent) {
        when (event) {
            is OrdersUiEvent.RefreshOrders -> refreshData()
        }
    }

    private fun refreshData() = coroutineScope.launch {
        orderService.refreshOrders()
    }

}