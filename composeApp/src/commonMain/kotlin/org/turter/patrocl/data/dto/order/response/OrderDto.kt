package org.turter.patrocl.data.dto.order.response

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val guid: String,
    val name: String,
    val sum: Float,
    val table: Table,
    val waiter: Waiter,
    val openTime: LocalDateTime,
    val sessions: List<Session>
) {
    @Serializable
    data class Waiter(
        val id: String,
        val code: String,
        val name: String
    )

    @Serializable
    data class Table(
        val id: String,
        val code: String,
        val name: String
    )

    @Serializable
    data class Session(
        val uni: String,
        val lineGuid: String,
        val sessionId: String,
        val isDraft: Boolean,
        val remindTime: LocalDateTime,
        val startService: LocalDateTime,
        val printed: Boolean,
        val cookMins: Int?,
        val creator: Waiter,
        val dishes: List<Dish>
    )

    @Serializable
    data class Dish(
        val id: String,
        val name: String,
        val quantity: Float,
        val code: String,
        val uni: String,
        val modifiers: List<Modifier>
    ) {
        @Serializable
        data class Modifier(
            val id: String,
            val name: String,
            val quantity: Int
        )
    }
}
