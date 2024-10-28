package org.turter.patrocl.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.domain.model.AuthState
import org.turter.patrocl.domain.model.Message
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.error.ErrorComponent
import org.turter.patrocl.presentation.main.components.AuthScreen
import org.turter.patrocl.presentation.main.components.MainErrorScreen
import org.turter.patrocl.presentation.main.components.SnackbarMessageHost

class MainScreen : Screen {
    @Composable
    override fun Content() {
        val vm: MainViewModel = getScreenModel()
        val authState by vm.authStateFlow.collectAsState()
        val mainScreenState by vm.mainScreenState.collectAsState()

        when (authState) {
            is AuthState.Authorized -> MainTabNavigator(
                screenState = mainScreenState,
//                messageState = vm.messageStateFlow.collectAsState(),
                logout = { vm.sendEvent(MainUiEvent.Logout) },
                refreshWaiter = { vm.sendEvent(MainUiEvent.RefreshWaiter) }
            )

            is AuthState.NotAuthorized -> AuthScreen(onLogin = { vm.sendEvent(MainUiEvent.Login) })

            else -> CircularLoader()
        }
    }

    @Composable
    private fun MainTabNavigator(
        screenState: MainScreenState,
//        messageState: State<Message<String>>,
        logout: () -> Unit,
        refreshWaiter: () -> Unit
    ) {
        when (screenState) {
            is MainScreenState.Content -> TabNavigator(
                OrdersTab(waiter = screenState.waiter)
            ) {
                Scaffold(
//                    snackbarHost = { SnackbarMessageHost(messageState = messageState) },
                    bottomBar = {
                        NavigationBar {
                            TabNavigatorItem(OrdersTab(waiter = screenState.waiter))
                            TabNavigatorItem(ProfileTab(logout = logout))
                        }
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        CurrentTab()
                        SnackbarMessageHost(
//                            messageState = messageState
                            message = screenState.message
                        )
                    }
                }
            }

            is MainScreenState.Error -> MainErrorScreen(
                errorType = screenState.errorType,
                onRetry = refreshWaiter
            )

            else -> CircularLoader()
        }

    }

    @Composable
    private fun RowScope.TabNavigatorItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current
        NavigationBarItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            label = { Text(tab.options.title) },
            icon = {
                val iconPainter =
                    tab.options.icon ?: rememberVectorPainter(Icons.Default.FavoriteBorder)
                Icon(painter = iconPainter, contentDescription = tab.options.title)
            }
        )
    }
}


