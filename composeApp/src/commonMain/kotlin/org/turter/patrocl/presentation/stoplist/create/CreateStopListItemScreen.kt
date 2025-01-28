package org.turter.patrocl.presentation.stoplist.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.koin.core.parameter.parametersOf
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.error.ErrorComponent

class CreateStopListItemScreen(
    private val currentList: List<StopListItem>
): Screen {
    @Composable
    override fun Content() {
        val vm: CreateStopListItemViewModel = koinScreenModel{ parametersOf(currentList) }

        when (val currentScreenState = vm.screenState.collectAsState().value) {
            is CreateStopListItemScreenState.Main -> {
                CreateStopListItemComponent(
                    vm = vm,
                    state = currentScreenState
                )
            }

            is CreateStopListItemScreenState.Error -> {
                ErrorComponent(
                    error = currentScreenState.errorType,
                    onRetry = { vm.sendEvent(CreateStopListItemUiEvent.Refresh) }
                )
            }

            else -> {
                CircularLoader()
            }
        }
    }
}