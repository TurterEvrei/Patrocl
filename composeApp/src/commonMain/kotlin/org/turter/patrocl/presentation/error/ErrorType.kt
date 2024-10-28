package org.turter.patrocl.presentation.error

import io.ktor.client.network.sockets.SocketTimeoutException
import org.turter.patrocl.domain.exception.ApiHttpException
import org.turter.patrocl.domain.exception.WebSocketSessionCloseException

sealed class ErrorType {
    object NetworkError : ErrorType()
    object WsCloseError : ErrorType()
    data class WsFailureError(val cause: Throwable?) : ErrorType()
    object ServerError : ErrorType()
    object UnknownError : ErrorType()
    object NotBindWaiterError : ErrorType()
    object NotBindEmployeeError : ErrorType()

    companion object Factory {
        fun from(throwable: Throwable) =
            when(val ex = throwable) {
                is ApiHttpException, is SocketTimeoutException -> NetworkError
                is WebSocketSessionCloseException -> WsCloseError
//                is WebSocketFailureException, is ProtocolException -> WsFailureError(cause = ex.cause)
                else -> UnknownError
            }
    }

    fun getMessage() =
        when (this) {
            is NetworkError -> "NetworkError"
            is ServerError -> "ServerError"
            is WsCloseError -> "WsCloseError"
            is WsFailureError -> "WsFailureError"
            is NotBindWaiterError -> "NotBindWaiterError"
            is NotBindEmployeeError -> "NotBindEmployeeError"
            else -> "Unknown error"
        }

    fun getAdvice() =
        when (this) {
            is NotBindWaiterError -> "NotBindWaiterError"
            is NotBindEmployeeError -> "NotBindEmployeeError"
            else -> "Call to support"
        }
}