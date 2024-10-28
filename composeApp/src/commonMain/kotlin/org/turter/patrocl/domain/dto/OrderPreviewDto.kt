package org.turter.patrocl.domain.dto

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
    val sum: Int,
    val bill: Boolean,
    val openTime: LocalDateTime,
) {
}