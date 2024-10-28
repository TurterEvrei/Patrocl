package org.turter.patrocl.presentation.main

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.AuthState
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.service.AuthService
import org.turter.patrocl.domain.service.MessageService
import org.turter.patrocl.domain.service.WaiterService
import org.turter.patrocl.presentation.error.ErrorType

sealed class MainUiEvent {
    data object Login : MainUiEvent()
    data object Logout : MainUiEvent()
    data object RefreshWaiter : MainUiEvent()
}

class MainViewModel(
    private val authService: AuthService,
    private val waiterService: WaiterService,
    private val messageService: MessageService
) : ScreenModel {
    private val log = Logger.withTag("MainViewModel")

    private val coroutineScope = screenModelScope

    val authStateFlow = authService.getAuthStateFlow()

    private val _mainScreenState = MutableStateFlow<MainScreenState>(MainScreenState.Initial)

    val mainScreenState: StateFlow<MainScreenState> = _mainScreenState.asStateFlow()

    init {
        coroutineScope.launch {
            combine(
                waiterService.getOwnWaiterStateFlow(),
                messageService.getMessageStateFlow()
            ) { waiterFetchState, message ->
                log.d { "Combine flows in init: \n" +
                        "-Waiter: $waiterFetchState \n" +
                        "-Message: $message" }
                when (waiterFetchState) {
                    is FetchState.Finished -> {
                        waiterFetchState.result.fold(
                            onSuccess = { waiter ->
                                MainScreenState.Content(
                                    waiter = waiter,
                                    message = message
                                )
                            },
                            onFailure = {
                                MainScreenState.Error(ErrorType.from(it))
                            }
                        )
                    }

                    else -> MainScreenState.Loading
                }
            }.collect { newMainScreenState ->
                _mainScreenState.value = newMainScreenState
            }
        }

        coroutineScope.launch {
            log.d { "Ping MainViewModel: ${this@MainViewModel}" }
        }

        coroutineScope.launch {
            messageService.getMessageStateFlow().collect { value ->
                log.d { "Collect _messageStateFlow value: $value" }
            }
        }

        coroutineScope.launch {
            log.d { "Launch collecting auth state to refresh waiter" }
            authStateFlow.collect { authState ->
                if (authState is AuthState.Authorized) {
                    log.d { "Auth state is Authorized - call refresh waiter from collecting auth state" }
                    waiterService.updateWaiterFromRemote()
                }
            }
        }
    }

    fun sendEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.Login -> authenticate()
            is MainUiEvent.Logout -> logout()
            is MainUiEvent.RefreshWaiter -> refreshWaiter()
        }
    }

    private fun authenticate() = coroutineScope.launch {
        authService.authenticate()
    }

    private fun logout() = coroutineScope.launch {
        authService.logout()
    }

    private fun refreshWaiter() = coroutineScope.launch {
        waiterService.updateWaiterFromRemote()
    }

}