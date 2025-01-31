package org.turter.patrocl.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import co.touchlab.kermit.Logger
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.main.MainScreen

//TODO remove
class InitialAuthScreen : Screen {
    private val log = Logger.withTag("AuthScreen")

    @Composable
    override fun Content() {
        val vm: AuthViewModel = koinScreenModel()
        val screenState by vm.screenState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        val state = screenState

//        AnimatedContent(
//            targetState = screenState,
//            transitionSpec = {
//                if (initialState is AuthScreenState.Loading || targetState is AuthScreenState.Loading) {
//                    fadeIn(initialAlpha = 0.4f) togetherWith fadeOut(targetAlpha = 0.4f) using SizeTransform(clip = false)
//                } else {
//                    if (targetState is AuthScreenState.NotAuthorized) {
//                            slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) togetherWith
//                                    slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) using
//                                    SizeTransform(clip = false)
//                    } else {
//                        slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) togetherWith
//                                slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) using
//                                SizeTransform(clip = false)
//                    }
//                }
//            }
//        ) { state ->
            when (state) {
//                is AuthScreenState.NotAuthorized -> Navigator(NotAuthorizedScreen(login = { vm.sendEvent(AuthUiEvent.Login) }))
                is WelcomeScreenState.NotAuthorized -> {
                    log.d { "Not authorized" }
                    navigator.replaceAll(WelcomeScreen())
                }

//                is AuthScreenState.Authorized -> Navigator(screen = MainScreen())
                is WelcomeScreenState.Authorized -> {
                    log.d { "Not authorized" }
                    navigator.replaceAll(MainScreen())
                }

                else -> CircularLoader()
            }
//        }

    }
}
