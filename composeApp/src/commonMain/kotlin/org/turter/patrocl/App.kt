package org.turter.patrocl

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.compose.KoinContext
import org.turter.patrocl.presentation.auth.AuthScreen
import org.turter.patrocl.presentation.orders.create.CreateOrderScreen
import org.turter.patrocl.ui.theme.AppTheme

@Composable
fun App() {
    KoinContext {
        AppTheme {
            Navigator(screen = AuthScreen())
//            Navigator(screen = CreateOrderScreen())
        }
    }
}