package org.turter.patrocl.presentation.orders.edit

import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.OrderWithDetails
import org.turter.patrocl.domain.model.order.SavedOrderItem
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.domain.model.source.MenuData
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.presentation.error.ErrorType

sealed class EditOrderScreenState {

    data object Initial: EditOrderScreenState()

    data object Loading: EditOrderScreenState()

    data class UpdateSavedOrder(
        val order: OrderWithDetails,
        val menuData: MenuData,
        val tables: List<Table>,
        val ownWaiter: Waiter,
        var newOrderItems: List<NewOrderItem>,
        var selectedTable: Table,
        var newOrderItemForDialog: NewOrderItem? = null,
        var expandedOrderItemDialog: Boolean = false,
        var orderItemForRemoving: SavedOrderItem? = null,
        var expandedConfirmRemovingOrderItem: Boolean = false
    ): EditOrderScreenState()

    data object RedirectToOrders: EditOrderScreenState()

    data class Error(val errorType: ErrorType): EditOrderScreenState()

}