package org.turter.patrocl.data.remote.client.impl

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.turter.patrocl.data.dto.person.EditOwnEmployeePayload
import org.turter.patrocl.data.remote.client.ApiEndpoint
import org.turter.patrocl.data.remote.client.proceedRequest
import org.turter.patrocl.data.remote.client.EmployeeApiClient
import org.turter.patrocl.data.dto.person.EmployeeDto

class EmployeeApiClientImpl(
    private val httpClient: HttpClient
): EmployeeApiClient {
    override suspend fun getOwnEmployee(): Result<EmployeeDto> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Employee.getOwnEmployee()) },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override suspend fun editOwnEmployee(payload: EditOwnEmployeePayload): Result<Unit> {
        return proceedRequest(
            action = { httpClient.patch(ApiEndpoint.Employee.editOwnEmployee()) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            } },
            decoder = { Json.decodeFromString(it.body()) }
        )
    }
}