package org.turter.patrocl.data.dto.order.response

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class OrderPreviewDto(
    val guid: String,
    val name: String,
    val tableCode: String,
    val tableName: String,
    val waiterCode: String,
    val waiterName: String,
    val sum: Float,
    val bill: Boolean,
    val openTime: LocalDateTime,
) {
}