package org.turter.patrocl.data.mapper.stoplist

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.turter.patrocl.data.dto.stoplist.StopListDto
import org.turter.patrocl.data.dto.stoplist.StopListItemDto
import org.turter.patrocl.domain.model.menu.Dish
import org.turter.patrocl.domain.model.stoplist.StopList
import org.turter.patrocl.domain.model.stoplist.StopListItem

fun StopListDto.toStopList(dishes: List<Dish>) =
    when(status) {
        StopListDto.Status.SUCCESS, StopListDto.Status.EMPTY -> StopList.Success(
            items = items.map { it.toStopListItem(dishes) }
        )

        StopListDto.Status.ERROR -> StopList.Error(message = message)
    }

fun StopListItemDto.toStopListItem(dishes: List<Dish>) = StopListItem(
    id = id,
    dishId = dishId,
    dishName = dishes.find { it.id == dishId }?.name?:"",
    onStop = onStop,
    remainingCount = remainingCount,
    until = until.toLocalDateTime(TimeZone.currentSystemDefault()),
    createdAt = createdAt
)