package org.turter.patrocl.data.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import org.turter.patrocl.domain.client.TableApiClient
import org.turter.patrocl.domain.dto.TableDto

class TableApiClientImpl(
    private val httpClient: HttpClient
): TableApiClient {
    override suspend fun getTables(): Result<List<TableDto>> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Hall.getTables()) },
            decoder = { Json.decodeFromString(it.body()) }
        )
}