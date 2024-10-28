package org.turter.patrocl.data.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import org.turter.patrocl.domain.client.WaiterApiClient
import org.turter.patrocl.domain.dto.EditOwnWaiterPayload
import org.turter.patrocl.domain.dto.WaiterDto

class WaiterApiClientImpl(
    private val httpClient: HttpClient
): WaiterApiClient {
    override suspend fun getOwnWaiter(): Result<WaiterDto> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Waiter.getOwnWaiter()) },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override suspend fun editOwnWaiter(payload: EditOwnWaiterPayload): Result<Unit> =
        proceedRequest(
            action = { httpClient.patch(ApiEndpoint.Waiter.editOwnEmployee()) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            } },
            decoder = { }
        )

}