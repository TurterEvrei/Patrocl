package org.turter.patrocl.domain.exception

class InvalidUserFromTokenException(token: String): RuntimeException(
    "Can`t extract user from token: $token"
) {
}