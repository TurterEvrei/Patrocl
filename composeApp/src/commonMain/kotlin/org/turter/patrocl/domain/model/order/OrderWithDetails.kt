package org.turter.patrocl.domain.model.order

import kotlinx.datetime.LocalDateTime

data class OrderWithDetails(
    val guid: String,
    val name: String,
    val table: Table,
    val waiter: Waiter,
    val openTime: LocalDateTime,
    val items: List<SavedOrderItem>
) {
    fun getFormattedDate(): String = "${openTime.hour}:${openTime.minute}"

    data class Waiter(
        val id: String,
        val code: String,
        val name: String
    )

    data class Table(
        val id: String,
        val code: String,
        val name: String
    )
}