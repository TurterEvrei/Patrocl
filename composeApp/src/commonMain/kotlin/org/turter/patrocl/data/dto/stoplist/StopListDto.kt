package org.turter.patrocl.data.dto.stoplist

import kotlinx.serialization.Serializable

@Serializable
data class StopListDto(
    val status: Status,
    val message: String,
    val items: List<StopListItemDto>
) {
    enum class Status {
        SUCCESS,
        EMPTY,
        ERROR
    }
}