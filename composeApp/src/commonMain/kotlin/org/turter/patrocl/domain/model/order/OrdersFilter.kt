package org.turter.patrocl.domain.model.order

import org.turter.patrocl.domain.model.person.Waiter

data class OrdersFilter(
    val onlyMine: Boolean = false,
    val onlyNotBilled: Boolean = false
) {
    fun filter(orders: List<OrderPreview>, waiter: Waiter): List<OrderPreview> =
        orders
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