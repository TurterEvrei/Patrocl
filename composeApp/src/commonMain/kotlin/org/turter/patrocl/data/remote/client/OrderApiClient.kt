package org.turter.patrocl.data.remote.client

import kotlinx.coroutines.flow.Flow
import org.turter.patrocl.data.dto.order.request.CreateOrderPayload
import org.turter.patrocl.data.dto.order.request.OrderSessionPayload
import org.turter.patrocl.data.dto.order.request.RemoveItemsFromOrderPayload
import org.turter.patrocl.data.dto.order.response.OrderDto
import org.turter.patrocl.data.dto.order.response.OrdersListApiResponse
import org.turter.patrocl.domain.model.order.OrderPreview

interface OrderApiClient {
    suspend fun getOrderByGuid(guid: String): Result<OrderDto>
    suspend fun getActiveOrders(): Result<List<OrderPreview>>
    suspend fun getActiveOrdersFlow(): Flow<Result<OrdersListApiResponse>>
    suspend fun createOrder(
        payload: CreateOrderPayload
    ): Result<OrderDto>
    suspend fun updateOrder(payload: OrderSessionPayload.AddDishes): Result<OrderDto>
    suspend fun removeItem(payload: RemoveItemsFromOrderPayload): Result<OrderDto>
}