package org.turter.patrocl.data_mock.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.turter.patrocl.domain.model.stoplist.StopList
import org.turter.patrocl.domain.model.stoplist.StopListItem
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object StopListDataSupplier {

    fun getStopList() = StopList.Success(
        items = listOf(
            StopListItem(
                id = "stop-list-item-id-1",
                dishId = "dish-id-1",
                dishName = "Цезарь с курицей",
                onStop = true,
                remainingCount = 0,
                until = Clock.System.now().plus(1.toDuration(DurationUnit.HOURS)).toLocalDateTime(
                    TimeZone.currentSystemDefault()),
                createdAt = Clock.System.now().minus(1.toDuration(DurationUnit.DAYS))
            ),
            StopListItem(
                id = "stop-list-item-id-2",
                dishId = "dish-id-3",
                dishName = "Буритто",
                onStop = false,
                remainingCount = 1,
                until = Instant.fromEpochMilliseconds(Long.MAX_VALUE).toLocalDateTime(
                    TimeZone.currentSystemDefault()),
                createdAt = Clock.System.now().minus(1.toDuration(DurationUnit.DAYS))
            )
        )
    )

    fun getStopListMutated() = StopList.Success(
        items = listOf(
            StopListItem(
                id = "stop-list-item-id-2",
                dishId = "dish-id-3",
                dishName = "Буритто",
                onStop = true,
                remainingCount = 0,
                until = Instant.fromEpochMilliseconds(Long.MAX_VALUE).toLocalDateTime(
                    TimeZone.currentSystemDefault()),
                createdAt = Clock.System.now().minus(1.toDuration(DurationUnit.DAYS))
            )
        )
    )

    fun getStopListItem() = StopListItem(
        id = "stop-list-item-id-3",
        dishId = "dish-id-4",
        dishName = "Куриная отбивная",
        onStop = true,
        remainingCount = 0,
        until = Instant.fromEpochMilliseconds(Long.MAX_VALUE).toLocalDateTime(
            TimeZone.currentSystemDefault()),
        createdAt = Clock.System.now().minus(1.toDuration(DurationUnit.DAYS))
    )

}