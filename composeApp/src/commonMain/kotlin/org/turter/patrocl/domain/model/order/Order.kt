package org.turter.patrocl.domain.model.order

import kotlinx.datetime.LocalDateTime

data class Order(
    val guid: String,
    val name: String,
    val sum: Float,
    val table: Table,
    val waiter: Waiter,
    val openTime: LocalDateTime,
    val sessions: List<Session>
) {
//    fun getFormattedDate(): String = "${openTime.hour}:${openTime.minute}"

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

    data class Dish(
        val id: String,
        val name: String,
        val quantity: Float,
        val code: String,
        val uni: String,
        val modifiers: List<Modifier>
    ) {
        data class Modifier(
            val id: String,
            val name: String,
            val quantity: Int
        )
    }
}