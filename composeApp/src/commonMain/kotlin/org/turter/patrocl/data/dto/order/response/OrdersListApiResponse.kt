package org.turter.patrocl.data.dto.order.response

import kotlinx.serialization.Serializable

@Serializable
data class OrdersListApiResponse(
    val orders: List<OrderPreviewDto>,
    val status: Status,
    val message: String
) {
    enum class Status {
        FILLED,
        EMPTY,
        ERROR
    }
}