package org.turter.patrocl.presentation.orders.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.koin.core.parameter.parametersOf
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.error.ErrorComponent
import org.turter.patrocl.presentation.orders.edit.EditOrderUiEvent.RefreshData
import org.turter.patrocl.presentation.orders.edit.components.EditOrderComponent

class EditOrderScreen(private val orderGuid: String): Screen {
    @Composable
    override fun Content() {
        val vm: EditOrderViewModel = koinScreenModel { parametersOf(orderGuid) }

        when (val currentScreenState = vm.screenState.collectAsState().value) {
            is EditOrderScreenState.Main -> {
                EditOrderComponent(
                    vm = vm,
                    state = currentScreenState
                )
            }

            is EditOrderScreenState.Error -> {
                ErrorComponent(
                    error = currentScreenState.errorType,
                    onRetry = { vm.sendEvent(RefreshData) }
                )
            }

            else -> { CircularLoader() }
        }
    }
}