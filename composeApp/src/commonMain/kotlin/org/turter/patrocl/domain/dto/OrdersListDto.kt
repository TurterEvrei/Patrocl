package org.turter.patrocl.domain.dto

import kotlinx.serialization.Serializable

@Serializable
class OrdersListDto(
    val orders: List<OrderPreviewDto>
) {
}