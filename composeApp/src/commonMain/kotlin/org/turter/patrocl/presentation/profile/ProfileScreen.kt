package org.turter.patrocl.presentation.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.error.ErrorComponent
import org.turter.patrocl.presentation.error.ErrorType
import org.turter.patrocl.presentation.profile.ProfileUiEvent.RefreshProfileInfoFromRemote
import org.turter.patrocl.presentation.profile.components.ProfileContent

data class ProfileScreen(
    val logout: () -> Unit
): Screen {
    @Composable
    override fun Content() {
        val vm: ProfileViewModel = getScreenModel()

        val screenState by vm.screenState.collectAsState()

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            when (val currentState = screenState) {
                is ProfileScreenState.Content -> ProfileContent(
                    vm = vm,
                    state = currentState,
                    logout = logout
                )

                is ProfileScreenState.Error -> ErrorComponent(
                    error = ErrorType.from(currentState.cause),
                    onRetry = { vm.sendEvent(RefreshProfileInfoFromRemote) }
                )

                else -> CircularLoader()
            }

        }
    }
}