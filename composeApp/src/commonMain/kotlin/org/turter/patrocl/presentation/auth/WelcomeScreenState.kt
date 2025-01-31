package org.turter.patrocl.presentation.auth

import org.turter.patrocl.domain.model.person.Employee
import org.turter.patrocl.domain.model.person.User

sealed class WelcomeScreenState {

    data object Initial : WelcomeScreenState()

    data object Loading : WelcomeScreenState()

    data class Authorized(val user: User) : WelcomeScreenState()

    data class Forbidden(val user: User, val cause: Throwable) : WelcomeScreenState()

    data class NoBindEmployee(val user: User, val cause: Throwable) : WelcomeScreenState()

    data class NoBindWaiter(
        val user: User,
        val employee: Employee,
        val cause: Throwable
    ) : WelcomeScreenState()

    data class NotAuthorized(val cause: Throwable) : WelcomeScreenState()

}