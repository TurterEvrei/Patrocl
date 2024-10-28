package org.turter.patrocl.data.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import org.turter.patrocl.domain.client.EmployeeApiClient
import org.turter.patrocl.domain.dto.EmployeeDto

class EmployeeApiClientImpl(
    private val httpClient: HttpClient
): EmployeeApiClient {
    override suspend fun getOwnEmployee(): Result<EmployeeDto> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Employee.getOwnEmployee()) },
            decoder = { Json.decodeFromString(it.body()) }
        )
}