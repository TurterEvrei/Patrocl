package org.turter.patrocl.presentation.orders.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.error.ErrorComponent
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.RefreshData
import org.turter.patrocl.presentation.orders.create.components.CreateOrderComponent

class CreateOrderScreen: Screen {
    @Composable
    override fun Content() {
        val vm: CreateOrderViewModel = koinScreenModel()

        when (val currentScreenState = vm.screenState.collectAsState().value) {
            is CreateOrderScreenState.Main -> {
                CreateOrderComponent(
                    vm = vm,
                    state = currentScreenState
                )
            }

            is CreateOrderScreenState.Error -> {
                ErrorComponent(
                    error = currentScreenState.errorType,
                    onRetry = { vm.sendEvent(RefreshData) }
                )
            }

            else -> { CircularLoader() }
        }
    }
}