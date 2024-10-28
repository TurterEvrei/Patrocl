package org.turter.patrocl.domain.exception

class TokenExpiredException(exp: Long, cur: Long): RuntimeException(
    "Token expired. Exp: $exp. Current: $cur"
) {
}