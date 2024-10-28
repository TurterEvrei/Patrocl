package org.turter.patrocl.domain.dto.response

import kotlinx.serialization.Serializable
import org.turter.patrocl.domain.dto.OrderPreviewDto

@Serializable
class OrdersListApiResponse(
    val orders: List<OrderPreviewDto>
) {
}