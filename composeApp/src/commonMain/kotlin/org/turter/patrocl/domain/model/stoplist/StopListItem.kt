package org.turter.patrocl.domain.model.stoplist

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime

data class StopListItem(
    val id: String,
    val dishId: String,
    val dishName: String,
    val onStop: Boolean,
    val remainingCount: Int,
    val until: LocalDateTime,
    val createdAt: Instant
)
