package org.turter.patrocl.data.remote.client

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import org.turter.patrocl.domain.exception.ApiHttpException
import org.turter.patrocl.domain.model.order.OrderPreview

suspend fun <T> proceedRequest(
    action: suspend () -> HttpResponse,
    decoder: suspend (HttpResponse) -> T
): Result<T> =
    try {
        val res = action()
        if (res.status.isSuccess()) {
            val content = decoder(res)
            Result.success(content)
        } else {
            Result.failure(ApiHttpException(res))
        }
    } catch (e: Exception) {
        when (e) {
            is HttpRequestTimeoutException -> Result.failure(e)
            else -> throw e
        }
    }
