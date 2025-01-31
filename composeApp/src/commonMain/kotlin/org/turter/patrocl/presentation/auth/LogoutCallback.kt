package org.turter.patrocl.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import org.turter.patrocl.presentation.components.CircularLoader

class LogoutCallback: Screen {
    val log = Logger.withTag("LogoutCallback")
    @Composable
    override fun Content() {
        val vm: AuthViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow
        log.d { "Nav items: ${navigator.items}" }
        log.d { "Nav parent: ${navigator.parent}" }
        LaunchedEffect(Unit) {
            vm.sendEvent(AuthUiEvent.Logout { navigator.replaceAll(WelcomeScreen()) } )
        }

        CircularLoader()
    }
}

fun Navigator.logout() {
    var navigator = this
    while (navigator.parent != null) navigator = navigator.parent!!
    navigator.replaceAll(LogoutCallback())
}
