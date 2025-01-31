package org.turter.patrocl.presentation.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.turter.patrocl.presentation.auth.components.EmployeeNotBoundPage
import org.turter.patrocl.presentation.auth.components.ForbiddenPage
import org.turter.patrocl.presentation.auth.components.LoginPage
import org.turter.patrocl.presentation.auth.components.WaiterNotBoundPage
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.main.MainScreen

class WelcomeScreen : Screen {
    @Composable
    override fun Content() {
        val vm: AuthViewModel = koinScreenModel()

        val navigator = LocalNavigator.currentOrThrow
        val screenState by vm.screenState.collectAsState()

        AnimatedContent(
            targetState = screenState,
            transitionSpec = {
                if (initialState is WelcomeScreenState.Loading || targetState is WelcomeScreenState.Loading) {
                    fadeIn(initialAlpha = 0.4f) togetherWith fadeOut(targetAlpha = 0.4f) using SizeTransform(
                        clip = false
                    )
                } else {
                    if (targetState is WelcomeScreenState.NotAuthorized) {
                        slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) togetherWith
                                slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) using
                                SizeTransform(clip = false)
                    } else {
                        slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) togetherWith
                                slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) using
                                SizeTransform(clip = false)
                    }
                }
            }
        ) { state ->
            when (state) {
                is WelcomeScreenState.Authorized -> navigator.replaceAll(MainScreen())

                is WelcomeScreenState.NotAuthorized -> LoginPage(
                    onLogin = { vm.sendEvent(AuthUiEvent.Login) }
                )

                is WelcomeScreenState.Forbidden -> ForbiddenPage()

                is WelcomeScreenState.NoBindEmployee -> EmployeeNotBoundPage()

                is WelcomeScreenState.NoBindWaiter -> WaiterNotBoundPage()

                else -> CircularLoader()
            }
        }
    }
}