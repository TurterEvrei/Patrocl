package org.turter.patrocl.domain.exception

class InvalidTokenException(token: String) : RuntimeException("Invalid token: $token") {
}