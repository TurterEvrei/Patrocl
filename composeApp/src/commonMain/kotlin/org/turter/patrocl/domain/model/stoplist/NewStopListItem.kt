package org.turter.patrocl.domain.model.stoplist

import kotlinx.datetime.LocalDateTime

data class NewStopListItem(
    val dishId: String,
    val remainingCount: Int,
    val until: LocalDateTime?
) {
}