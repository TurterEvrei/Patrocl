package org.turter.patrocl.presentation.auth

import org.turter.patrocl.domain.model.AuthState
import org.turter.patrocl.domain.model.person.User

sealed class AuthScreenState {

    data object Initial: AuthScreenState()

    data object Loading: AuthScreenState()

    data class Authorized(val user: User): AuthScreenState()

    data class NotAuthorized(val cause: Throwable): AuthScreenState()

}