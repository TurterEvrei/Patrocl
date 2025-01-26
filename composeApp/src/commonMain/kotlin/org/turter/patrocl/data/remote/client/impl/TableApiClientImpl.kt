package org.turter.patrocl.data.remote.client.impl

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import org.turter.patrocl.data.remote.client.ApiEndpoint
import org.turter.patrocl.data.remote.client.proceedRequest
import org.turter.patrocl.data.remote.client.TableApiClient
import org.turter.patrocl.data.dto.source.TableDto

class TableApiClientImpl(
    private val httpClient: HttpClient
): TableApiClient {
    override suspend fun getTables(): Result<List<TableDto>> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Hall.getTables()) },
            decoder = { Json.decodeFromString(it.body()) }
        )
}