package org.turter.patrocl.domain.model

import org.turter.patrocl.domain.model.person.User

sealed class AuthState {

    data object Initial: AuthState()

    data object Loading: AuthState()

    data class Authorized(val user: User): AuthState()

    data class NotAuthorized(val cause: Throwable): AuthState()

}