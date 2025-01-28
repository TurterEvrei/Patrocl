package org.turter.patrocl.presentation.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import co.touchlab.kermit.Logger
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.main.components.MainErrorScreen
import org.turter.patrocl.presentation.main.components.SnackbarMessageHost

class MainScreen : Screen {
    private val log = Logger.withTag("MainScreen")

    @Composable
    override fun Content() {
        val vm: MainViewModel = koinScreenModel()
        val screenState by vm.mainScreenState.collectAsState()

        AnimatedContent(
            targetState = screenState,
            transitionSpec = {
                fadeIn(initialAlpha = 0.4f) togetherWith
                        fadeOut(targetAlpha = 0.4f) using SizeTransform(clip = false)
            }
        ) { state ->
            when (state) {
                is MainScreenState.Content -> {
                    val tabs = listOf(
                        StopListTab,
                        OrdersTab(waiter = state.waiter),
                        ProfileTab(logout = { vm.sendEvent(MainUiEvent.Logout) })
                    )
                    TabNavigator(tab = OrdersTab(waiter = state.waiter)) {
                        Scaffold(
                            bottomBar = {
                                NavigationBar {
                                    tabs.forEach { TabNavigatorItem(it) }
                                }
                            }
                        ) { paddingValues ->
                            Box(modifier = Modifier.padding(paddingValues)) {
                                CurrentTab()
                                SnackbarMessageHost(
                                    messageState = state.messageState.collectAsState()
                                )
                            }
                        }
                    }
                }

                is MainScreenState.Error -> MainErrorScreen(
                    errorType = state.errorType,
                    onRetry = { vm.sendEvent(MainUiEvent.RefreshWaiter) }
                )

                else -> CircularLoader()
            }
        }
    }


    @Composable
    private fun RowScope.TabNavigatorItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current
        NavigationBarItem(
            selected = tabNavigator.current == tab,
            onClick = {
                log.d { "Switch to tab: $tab" }
                tabNavigator.current = tab
            },
            label = { Text(tab.options.title) },
            icon = {
                val iconPainter =
                    tab.options.icon ?: rememberVectorPainter(Icons.Default.FavoriteBorder)
                Icon(painter = iconPainter, contentDescription = tab.options.title)
            }
        )
    }
}