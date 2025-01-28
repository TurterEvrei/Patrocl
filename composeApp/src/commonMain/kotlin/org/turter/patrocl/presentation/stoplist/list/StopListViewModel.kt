package org.turter.patrocl.presentation.stoplist.list

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.service.StopListService
import org.turter.patrocl.presentation.error.ErrorType

sealed class StopListUiEvent {
    data class SelectItem(val id: String): StopListUiEvent()
    data class UnselectItem(val id: String): StopListUiEvent()
    data object UnselectAllItems: StopListUiEvent()
    data object RemoveSelectedItems: StopListUiEvent()
    data object RefreshList: StopListUiEvent()
}

class StopListViewModel(
    private val stopListService: StopListService
) : ScreenModel {
    private val coroutineScope = screenModelScope
    private val log = Logger.withTag("StopListViewModel")

    private val _screenState = MutableStateFlow<StopListScreenState>(StopListScreenState.Initial)

    val screenState: StateFlow<StopListScreenState> = _screenState.asStateFlow()

    init {
        coroutineScope.launch {
            stopListService.getStopListStateFlow()
                .collect { fetchState ->
                    _screenState.value = when (fetchState) {
                        is FetchState.Finished -> fetchState.result.fold(
                            onSuccess = { StopListScreenState.Main(items = it.items) },
                            onFailure = { StopListScreenState.Error(errorType = ErrorType.from(it)) }
                        )

                        else -> StopListScreenState.Loading
                    }
                }
        }
    }

    fun sendEvent(event: StopListUiEvent) =
        when(event) {
            is StopListUiEvent.SelectItem -> selectItem(id = event.id)
            is StopListUiEvent.UnselectItem -> unselectItem(id = event.id)
            is StopListUiEvent.UnselectAllItems -> unselectAllItems()
            is StopListUiEvent.RemoveSelectedItems -> removeSelectedItems()
            is StopListUiEvent.RefreshList -> refreshList()
        }

    private fun selectItem(id: String) {
        withMainState()?.selectedItemsIds?.apply { if (!contains(id)) add(id) }
    }

    private fun unselectItem(id: String) {
        withMainState()?.selectedItemsIds?.remove(id)
    }

    private fun unselectAllItems() {
        withMainState()?.selectedItemsIds?.clear()
    }

    private fun removeSelectedItems() {
        withMainState()?.selectedItemsIds?.let { ids ->
            coroutineScope.launch {
                setRemoving(true)
                stopListService.removeItems(ids)
                    .onSuccess { unselectAllItems() }
                setRemoving(false)
            }
        }
    }

    private fun refreshList() {
        coroutineScope.launch { stopListService.refreshStopList() }
    }

    private fun setRemoving(value: Boolean) {
        log.d { "Set isRemoving to $value" }
        transformMainState { it.copy(isRemoving = value) }
    }

    private fun transformMainState(
        action: (state: StopListScreenState.Main) -> StopListScreenState
    ) {
        val currentState = _screenState.value
        if (currentState is StopListScreenState.Main) {
            _screenState.value = action(currentState)
        }
    }

    private fun withMainState(): StopListScreenState.Main? {
        val state = _screenState.value
        return if (state is StopListScreenState.Main) state else null
    }
}