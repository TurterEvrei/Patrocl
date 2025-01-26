package org.turter.patrocl.data.mapper.stoplist

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import org.turter.patrocl.data.dto.stoplist.CreateStopListItemPayload
import org.turter.patrocl.data.dto.stoplist.EditStopListItemPayload
import org.turter.patrocl.domain.model.stoplist.NewStopListItem

fun NewStopListItem.toCreateStopListItemPayload() = CreateStopListItemPayload(
    dishId = dishId,
    remainingCount = remainingCount,
    until = until?.toInstant(TimeZone.currentSystemDefault())
)

fun toEditStopListItemPayload(
    id: String,
    remainingCount: Int,
    until: LocalDateTime?
) = EditStopListItemPayload(
    id = id,
    remainingCount = remainingCount,
    until = until?.toInstant(TimeZone.currentSystemDefault())
)