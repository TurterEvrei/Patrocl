package org.turter.patrocl.data.dto.order.request

import kotlinx.serialization.Serializable

@Serializable
data class RemoveItemsFromOrderPayload(
    val orderGuid: String,
    val sessions: List<OrderSessionPayload.RemoveDishes>
)