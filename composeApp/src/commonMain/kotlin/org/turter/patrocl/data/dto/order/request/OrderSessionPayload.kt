package org.turter.patrocl.data.dto.order.request

import kotlinx.serialization.Serializable

sealed interface OrderSessionPayload {
    val items: List<OrderItemPayload>

    @Serializable
    data class AddDishes(
        val orderGuid: String,
        override val items: List<OrderItemPayload.Add>
    ) : OrderSessionPayload

    @Serializable
    data class RemoveDishes(
        val sessionUni: String,
        override val items: List<OrderItemPayload.Remove>
    ) : OrderSessionPayload
}
