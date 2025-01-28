package org.turter.patrocl.presentation.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.Navigator
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.main.MainScreen

class AuthScreen : Screen {

    @Composable
    override fun Content() {
        val vm: AuthViewModel = koinScreenModel()
        val screenState by vm.screenState.collectAsState()

        AnimatedContent(
            targetState = screenState,
            transitionSpec = {
                if (initialState is AuthScreenState.Loading || targetState is AuthScreenState.Loading) {
                    fadeIn(initialAlpha = 0.4f) togetherWith fadeOut(targetAlpha = 0.4f) using SizeTransform(clip = false)
                } else {
                    if (targetState is AuthScreenState.NotAuthorized) {
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
                is AuthScreenState.NotAuthorized -> Scaffold {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier.padding(end = 30.dp),
                            text = "TurterApp",
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize
                        )
                        Text(
                            modifier = Modifier.padding(start = 14.dp),
                            text = "Blue `da Nos",
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize
                        )
                        Spacer(Modifier.height(18.dp))
                        Button(
                            onClick = { vm.sendEvent(AuthUiEvent.Login) }
                        ) {
                            Text(
                                text = "Войти".uppercase()
                            )
                        }
                    }
                }

                is AuthScreenState.Authorized -> Navigator(screen = MainScreen())

                else -> CircularLoader()
            }
        }

    }
}
