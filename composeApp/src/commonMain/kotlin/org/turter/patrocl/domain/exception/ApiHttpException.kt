package org.turter.patrocl.domain.exception

import io.ktor.client.statement.HttpResponse

class ApiHttpException(private val response: HttpResponse): RuntimeException(
    "Failure response from api. Response: $response"
) {
}