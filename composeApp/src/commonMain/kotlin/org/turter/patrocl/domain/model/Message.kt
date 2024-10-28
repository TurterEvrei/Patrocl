package org.turter.patrocl.domain.model

import kotlinx.datetime.LocalDateTime
import org.turter.patrocl.utils.now

sealed class Message<out T>() {

    data object Initial : Message<Nothing>()

    data class Content<out T>(
        val content: T,
        val initTime: LocalDateTime = LocalDateTime.now()
    ) : Message<T>()

    data class Success<out T>(
        val content: T,
        val initTime: LocalDateTime = LocalDateTime.now()
    ) : Message<T>()

    data class Error<out T>(
        val exception: Throwable,
        val initTime: LocalDateTime = LocalDateTime.now()
    ) : Message<T>()

    companion object Factory {
        fun <T> initial(): Message<T> = Initial
        fun <T> content(content: T): Message<T> = Content(content)
        fun <T> success(content: T): Message<T> = Success(content)
        fun <T> error(exception: Throwable): Message<T> = Error(exception)
    }

}