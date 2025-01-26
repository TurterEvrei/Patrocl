package org.turter.patrocl.presentation.orders.list

import org.turter.patrocl.domain.model.order.OrderPreview
import org.turter.patrocl.domain.model.person.Waiter

//TODO move to presentation package
data class OrdersFilter(
    val searchName: String = "",
    val onlyMine: Boolean = false,
    val onlyNotBilled: Boolean = false
) {
    fun filter(orders: List<OrderPreview>, waiter: Waiter): List<OrderPreview> =
        orders
            .filter { order ->
                if (searchName.isNotEmpty()) order.name.contains(searchName)
                else true
            }
            .filter { order ->
                if (onlyMine) order.waiterCode == waiter.code
                else true
            }
            .filter { order ->
                if (onlyNotBilled) !order.bill
                else true
            }
            .toList()
}