package org.turter.patrocl.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun LocalDateTime.Companion.now(): LocalDateTime =
    Clock.System.now().toLocalDateTime(timeZone = TimeZone.currentSystemDefault())

fun LocalDateTime.isSoon() = this.inDuration(30.toDuration(DurationUnit.DAYS))

fun LocalDateTime.inDuration(duration: Duration) =
    this < Clock.System.now()
        .plus(duration)
        .toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDateTime.toFormatString(): String {
    return "${dayOfMonth.printTwoDigits()}.${monthNumber.printTwoDigits()}.${year} " +
            "${hour.printTwoDigits()}:${minute.printTwoDigits()}"
}

fun LocalDateTime.toFormatTime(): String {
    return "${hour.printTwoDigits()}:${minute.printTwoDigits()}:${second.printTwoDigits()}"
}

private fun Int.printTwoDigits() = this.toString().let { if (it.length < 2) "0$it" else it }