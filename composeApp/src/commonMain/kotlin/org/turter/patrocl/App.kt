package org.turter.patrocl

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.koin.compose.KoinContext
import org.turter.patrocl.presentation.auth.WelcomeScreen
import org.turter.patrocl.ui.theme.AppTheme

@Composable
fun App() {
    KoinContext {
        AppTheme {
            Navigator(WelcomeScreen())
        }
    }
}