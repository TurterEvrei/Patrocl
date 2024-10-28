package org.turter.patrocl.presentation.orders.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.error.ErrorComponent
import org.turter.patrocl.presentation.orders.create.CreateOrderUiEvent.RefreshData
import org.turter.patrocl.presentation.orders.create.components.CreateOrderComponent

class CreateOrderScreen: Screen {
    @Composable
    override fun Content() {
        val vm: CreateOrderViewModel = getScreenModel()
        val navigator = LocalNavigator.currentOrThrow

        when (val currentScreenState = vm.screenState.collectAsState().value) {
            is CreateOrderScreenState.CreateNewOrder -> {
                CreateOrderComponent(
                    vm = vm,
                    currentScreenState = currentScreenState
                )
            }

            is CreateOrderScreenState.Error -> {
                ErrorComponent(
                    error = currentScreenState.errorType,
                    onRetry = { vm.sendEvent(RefreshData) }
                )
            }

            is CreateOrderScreenState.RedirectToOrders -> navigator.popUntilRoot()

            else -> { CircularLoader() }
        }
    }
}