package org.turter.patrocl.presentation.stoplist.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.error.ErrorComponent
import org.turter.patrocl.presentation.stoplist.create.CreateStopListItemScreen
import org.turter.patrocl.presentation.stoplist.edit.EditStopListItemScreen
import org.turter.patrocl.presentation.stoplist.list.components.StopListComponent

class StopListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val vm: StopListViewModel = getScreenModel()

        when (val currentScreenState = vm.screenState.collectAsState().value) {
            is StopListScreenState.Main -> {
                StopListComponent(
                    vm = vm,
                    state = currentScreenState,
                    onCreateItem = {
                        navigator.push(
                            CreateStopListItemScreen(currentList = currentScreenState.items)
                        )
                    },
                    onOpenItem = { navigator.push(EditStopListItemScreen(targetItem = it)) }
                )
            }

            is StopListScreenState.Error -> {
                ErrorComponent(
                    error = currentScreenState.errorType,
                    onRetry = { vm.sendEvent(StopListUiEvent.RefreshList) }
                )
            }

            else -> {
                CircularLoader()
            }
        }
    }
}