package org.turter.patrocl.domain.model

import org.turter.patrocl.domain.model.person.Employee
import org.turter.patrocl.domain.model.person.User

sealed class AuthState {

    data object Initial: AuthState()

    data object Loading: AuthState()

    data class Authorized(val user: User): AuthState()

    data class Forbidden(val user: User, val cause: Throwable): AuthState()

    data class NoBindEmployee(val user: User, val cause: Throwable): AuthState()

    data class NoBindWaiter(val user: User, val employee: Employee, val cause: Throwable): AuthState()

    data class NotAuthorized(val cause: Throwable): AuthState()

}