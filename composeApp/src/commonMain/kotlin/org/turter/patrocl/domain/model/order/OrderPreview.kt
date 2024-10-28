package org.turter.patrocl.domain.model.order

import kotlinx.datetime.LocalDateTime

data class OrderPreview(
    val guid: String,
    val name: String,
    val tableCode: String,
    val tableName: String,
    val waiterCode: String,
    val waiterName: String,
    val sum: Int,
    val bill: Boolean,
    val openTime: LocalDateTime,
) {
    fun getFormattedSum() = sum / 100f

    fun getFormattedDate(): String = "${openTime.hour}:${openTime.minute}"
}
