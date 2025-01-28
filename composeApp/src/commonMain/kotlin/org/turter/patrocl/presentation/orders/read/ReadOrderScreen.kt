package org.turter.patrocl.presentation.orders.read

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.core.parameter.parametersOf
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.error.ErrorComponent
import org.turter.patrocl.presentation.orders.read.components.ReadOrderComponent

class ReadOrderScreen(private val orderGuid: String): Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: ReadOrderViewModel = koinScreenModel { parametersOf(orderGuid, navigator) }

        when (val currentScreenState = vm.screenState.collectAsState().value) {
            is ReadOrderScreenState.Main -> {
                ReadOrderComponent(
                    vm = vm,
                    state = currentScreenState
                )
            }

            is ReadOrderScreenState.Error -> {
                ErrorComponent(
                    error = currentScreenState.errorType,
                    onRetry = { vm.sendEvent(ReadOrderUiEvent.RefreshOrder) }
                )
            }

            else -> { CircularLoader() }
        }
    }
}