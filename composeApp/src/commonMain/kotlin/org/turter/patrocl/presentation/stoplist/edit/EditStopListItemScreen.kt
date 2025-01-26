package org.turter.patrocl.presentation.stoplist.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.core.parameter.parametersOf
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.error.ErrorComponent

class EditStopListItemScreen(
    private val targetItem: StopListItem
): Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: EditStopListItemViewModel = getScreenModel{ parametersOf(targetItem, navigator) }

        when (val currentScreenState = vm.screenState.collectAsState().value) {
            is EditStopListItemScreenState.Main -> {
                EditStopListItemComponent(
                    vm = vm,
                    state = currentScreenState
                )
            }

//            is EditStopListItemScreenState.Error -> {
//                ErrorComponent(
//                    error = currentScreenState.errorType,
//                    onRetry = { vm.sendEvent(CreateStopListItemUiEvent.Refresh) }
//                )
//            }

            else -> {
                CircularLoader()
            }
        }
    }
}