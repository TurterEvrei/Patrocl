package org.turter.patrocl.presentation.orders.list

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.service.OrderService
import org.turter.patrocl.presentation.error.ErrorType

sealed class OrdersUiEvent {
    data object RefreshOrders : OrdersUiEvent()
}

class OrdersViewModel(
    private val orderService: OrderService
) : ScreenModel {

    private val coroutineScope = screenModelScope

    private val _screenState = MutableStateFlow<OrdersScreenState>(OrdersScreenState.Initial)

    val screenState: StateFlow<OrdersScreenState> = _screenState

    init {
        coroutineScope.launch {
            orderService.getActiveOrdersStateFlow()
                .collect { fetchState ->
                    when (fetchState) {
                        is FetchState.Finished -> {
                            _screenState.emit(
                                fetchState.result.fold(
                                    onSuccess = {
                                        OrdersScreenState.Content(orders = it)
                                    },
                                    onFailure = {
                                        OrdersScreenState.Error(errorType = ErrorType.from(it))
                                    }
                                )
                            )
                        }

                        else -> OrdersScreenState.Loading
                    }
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