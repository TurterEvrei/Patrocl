package org.turter.patrocl.presentation.main

import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.domain.model.Message
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.presentation.error.ErrorType

sealed class MainScreenState {
    data object Initial: MainScreenState()

    data object Loading: MainScreenState()

    data class Content(
        val waiter: Waiter,
        val messageState: StateFlow<Message<String>>
    ): MainScreenState()

    data class Error(val errorType: ErrorType): MainScreenState()
}