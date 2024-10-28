package org.turter.patrocl.domain.exception

import io.ktor.http.HttpStatusCode

class EndSessionFailureHttpStatusCode(code: Int): RuntimeException(
    "Failure status code from end session request: $code"
) {
}