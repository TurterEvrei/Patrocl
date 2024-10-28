package org.turter.patrocl.domain.service

import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.OrderPreview
import org.turter.patrocl.domain.model.order.OrderWithDetails
import org.turter.patrocl.domain.model.order.SavedOrderItem
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.domain.model.source.Table

interface OrderService {
    fun getOrderFlow(guid: String): StateFlow<FetchState<OrderWithDetails>>
    fun getActiveOrdersStateFlow(): StateFlow<FetchState<List<OrderPreview>>>
    suspend fun refreshOrders()
    suspend fun createOrder(
        table: Table,
        waiter: Waiter,
        orderItems: List<NewOrderItem>
    ): Result<OrderWithDetails>

    suspend fun updateOrder(
        orderGuid: String,
        waiterCode: String,
        tableCode: String,
        orderItems: List<NewOrderItem>
    ): Result<OrderWithDetails>

    suspend fun removeItemFromOrder(
        orderGuid: String,
        orderItem: SavedOrderItem,
        quantity: Float
    ): Result<Unit>
}