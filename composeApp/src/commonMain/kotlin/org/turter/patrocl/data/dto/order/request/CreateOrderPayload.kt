package org.turter.patrocl.data.dto.order.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderPayload(
    val tableCode: String,
    val waiterCode: String,
    val dishList: List<OrderItemPayload.Add>
)

