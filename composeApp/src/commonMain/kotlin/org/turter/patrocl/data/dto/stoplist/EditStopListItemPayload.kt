package org.turter.patrocl.data.dto.stoplist

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class EditStopListItemPayload(
    val id: String,
    val remainingCount: Int,
    val until: Instant?
)
