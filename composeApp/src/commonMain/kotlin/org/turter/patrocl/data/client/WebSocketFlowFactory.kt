package org.turter.patrocl.data.client

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.serialization.JsonConvertException
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.exception.WebSocketSessionCloseException
import org.turter.patrocl.domain.service.AuthService

class WebSocketFlowFactory(
    private val httpClient: HttpClient,
    private val authService: AuthService
) {
    private val log = Logger.withTag("WebSocketFactory")

    fun <T> create(
        path: String,
        onError: (Exception) -> Unit = {},
        decoder: (String) -> T,
    ): Flow<Result<T>> = callbackFlow {
        val producerScope = this
        val coroutineScope = CoroutineScope(Dispatchers.IO)

        authService.updateTokenIfExpired()

        try {
            val session = httpClient.webSocketSession(path)

            log.d { "Starting websocket session for path: $path" }
            session.incoming.consumeAsFlow()
                .collect { frame ->
                    log.d { "Incoming frame: $frame, from path: $path" }
                    if (frame is Frame.Text) {
                        val text = frame.readText()
                        try {
                            val result = decoder(text)
                            log.d { "Successfully decoded message: $result" }
                            trySend(Result.success(result))
                        } catch (e: JsonConvertException) {
                            log.e { "Error decoding JSON: $e" }
                            trySend(Result.failure(e))
                        }
                    }
                }

            awaitClose {
                log.d { "Closing flow - WebSocket session is ending" }
                coroutineScope.launch {
                    session.close(CloseReason(CloseReason.Codes.NORMAL, "Flow completed"))
                }
            }
        } catch (e: Exception) {
            log.e { "Exception in WebSocket session for path: $path, exception: $e" }
            e.printStackTrace()
            onError(e)
            trySend(Result.failure(e))
            close(e)
        }

//        httpClient.webSocket(path) {
//            log.d { "Starting websocket connection for path: $path" }
//            try {
//                incoming.consumeAsFlow()
//                    .map { frame ->
//                        log.d { "Incoming frame: $frame, from path: $path" }
//                        if (frame is Frame.Text) {
//                            val string = frame.readText()
//                            try {
//                                val result = decoder(string)
//                                log.d { "Successful decoded message: $result" }
//                                trySend(Result.success(result))
//                            } catch (e: JsonConvertException) {
//                                log.e { "Error on converting json: $e" }
//                                trySend(Result.failure(e))
//                            }
//                        }
//                    }
//            } catch (e: Exception) {
//                log.e { "Catch exception in websocket for path: $path, exception: $e" }
//                e.printStackTrace()
//                onError(e)
//                trySend(Result.failure(e))
//            } finally {
//                val closeReason = closeReason.await()
//                log.d { "Closing websocket for path: $path, reason: $closeReason" }
//                producerScope.close(WebSocketSessionCloseException())
//            }
//
//            awaitClose {
//                log.d { "Closing flow - flow completed" }
//                launch {
//                    close(CloseReason(CloseReason.Codes.NORMAL, "Flow completed"))
//                }
//            }
//
//        }
    }
}