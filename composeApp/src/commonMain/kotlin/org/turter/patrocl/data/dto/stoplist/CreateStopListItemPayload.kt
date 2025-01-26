package org.turter.patrocl.data.dto.stoplist

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CreateStopListItemPayload(
    val dishId: String,
    val remainingCount: Int,
    val until: Instant?
)
