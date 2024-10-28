package org.turter.patrocl.presentation.orders.create

import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.domain.model.source.MenuData
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.presentation.error.ErrorType

sealed class CreateOrderScreenState {

    data object Initial: CreateOrderScreenState()

    data object Loading: CreateOrderScreenState()

    data class CreateNewOrder(
        val menuData: MenuData,
        val tables: List<Table>,
        val ownWaiter: Waiter,
        var newOrderItems: List<NewOrderItem>,
        var selectedTable: Table? = null,
        var newOrderItemForDialog: NewOrderItem? = null,
        var expandedOrderItemDialog: Boolean = false
    ): CreateOrderScreenState()

    data object RedirectToOrders: CreateOrderScreenState()

    data class Error(val errorType: ErrorType): CreateOrderScreenState()

}