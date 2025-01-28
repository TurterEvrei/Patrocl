package org.turter.patrocl.presentation.stoplist.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.koin.core.parameter.parametersOf
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.presentation.components.CircularLoader

class EditStopListItemScreen(
    private val targetItem: StopListItem
): Screen {
    @Composable
    override fun Content() {
        val vm: EditStopListItemViewModel = koinScreenModel{ parametersOf(targetItem) }

        when (val currentScreenState = vm.screenState.collectAsState().value) {
            is EditStopListItemScreenState.Main -> {
                EditStopListItemComponent(
                    vm = vm,
                    state = currentScreenState
                )
            }

            else -> {
                CircularLoader()
            }
        }
    }
}