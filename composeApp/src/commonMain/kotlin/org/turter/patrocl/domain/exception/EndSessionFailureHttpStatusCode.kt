package org.turter.patrocl.domain.exception

class EndSessionFailureHttpStatusCode(code: Int): RuntimeException(
    "Failure status code from end session request: $code"
) {
}