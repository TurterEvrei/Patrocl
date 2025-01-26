package org.turter.patrocl.presentation.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.AuthState
import org.turter.patrocl.domain.service.AuthService

sealed class AuthUiEvent {
    data object Login: AuthUiEvent()
    data object Logout: AuthUiEvent()
}

class AuthViewModel(
    private val authService: AuthService
): ScreenModel {
    private val _screenState = MutableStateFlow<AuthScreenState>(AuthScreenState.Initial)

    val screenState: StateFlow<AuthScreenState> = _screenState.asStateFlow()

    init {
        screenModelScope.launch {
            authService.getAuthStateFlow()
                .collect { authState ->
                    _screenState.value = when(authState) {
                        is AuthState.Authorized -> AuthScreenState.Authorized(user = authState.user)
                        is AuthState.NotAuthorized -> AuthScreenState.NotAuthorized(cause = authState.cause)
                        else -> AuthScreenState.Loading
                    }
                }
        }
    }

    fun sendEvent(event: AuthUiEvent) {
        when(event) {
            is AuthUiEvent.Login -> login()
            is AuthUiEvent.Logout -> logout()
        }
    }

    private fun login() {
        screenModelScope.launch {
            authService.authenticate()
        }
    }

    private fun logout() {
        screenModelScope.launch {
            authService.logout()
        }
    }

}