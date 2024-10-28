package org.turter.patrocl.domain.client

import kotlinx.coroutines.flow.Flow
import org.turter.patrocl.domain.dto.OrderPayload
import org.turter.patrocl.domain.dto.OrderWithDetailsDto
import org.turter.patrocl.domain.model.order.OrderPreview

interface OrderApiClient {
    suspend fun getOrderByGuid(guid: String): Result<OrderWithDetailsDto>
    suspend fun getActiveOrders(): Result<List<OrderPreview>>
    fun getActiveOrdersFlow(): Flow<Result<List<OrderPreview>>>
    suspend fun createOrder(
        payload: OrderPayload
    ): Result<OrderWithDetailsDto>
    suspend fun updateOrder(
        guid: String,
        payload: OrderPayload
    ): Result<OrderWithDetailsDto>
    suspend fun removeItem(
        orderGuid: String,
        sessionUni: String,
        dishUni: String,
        dishCode: String,
        quantity: Float,
    ): Result<Unit>
}