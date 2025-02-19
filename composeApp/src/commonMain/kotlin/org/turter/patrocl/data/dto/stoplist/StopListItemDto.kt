package org.turter.patrocl.data.dto.stoplist

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class StopListItemDto(
    val id: String,
    val dishId: String,
    val companyId: String,
    val onStop: Boolean,
    val remainingCount: Int,
    val until: String,
    val createdAt: Instant,
    val createdBy: String
)