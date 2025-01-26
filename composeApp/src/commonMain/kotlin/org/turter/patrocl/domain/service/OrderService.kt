package org.turter.patrocl.domain.service

import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.data.dto.order.response.OrdersListApiResponse
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.OrderPreview
import org.turter.patrocl.domain.model.order.Order
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.domain.model.source.Table

interface OrderService {
    fun getOrderFlow(guid: String): StateFlow<FetchState<Order>>
    fun getActiveOrdersStateFlow(): StateFlow<FetchState<List<OrderPreview>>>
    suspend fun refreshOrders()
    suspend fun createOrder(
        table: Table,
        waiter: Waiter,
        orderItems: List<NewOrderItem>
    ): Result<Order>

    suspend fun addItemsToOrder(
        orderGuid: String,
        newItems: List<NewOrderItem>
    ): Result<Order>

    suspend fun removeItemFromOrderSession(
        orderGuid: String,
        payload: Order.Session
    ): Result<Order>

    suspend fun removeItemsFromOrderSessions(
        orderGuid: String,
        payload: List<Order.Session>
    ): Result<Order>
}