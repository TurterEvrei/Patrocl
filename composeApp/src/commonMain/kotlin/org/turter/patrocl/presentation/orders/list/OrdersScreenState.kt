package org.turter.patrocl.presentation.orders.list

import org.turter.patrocl.domain.model.order.OrderPreview
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.presentation.error.ErrorType

sealed class OrdersScreenState {
    data object Initial: OrdersScreenState()

    data object Loading: OrdersScreenState()

    data class Content(
        val orders: List<OrderPreview>,
        val waiter: Waiter
    ) : OrdersScreenState()

    data class Error(
        val errorType: ErrorType
    ) : OrdersScreenState()
}