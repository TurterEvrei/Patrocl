package org.turter.patrocl.domain.dto

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class OrderWithDetailsDto(
    val guid: String,
    val name: String,
    val table: TableDto,
    val waiter: WaiterDto,
    val openTime: LocalDateTime,
    val items: List<OrderItemDto>
) {
    @Serializable
    data class WaiterDto(
        val id: String,
        val code: String,
        val name: String
    )

    @Serializable
    data class TableDto(
        val id: String,
        val code: String,
        val name: String
    )
}
