package org.turter.patrocl.presentation.stoplist.edit

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.domain.service.StopListService
import org.turter.patrocl.utils.now

sealed class EditStopListItemUiEvent {
    data class SetRemainCount(val count: Int) : EditStopListItemUiEvent()
    data class SetUntil(val value: LocalDateTime?) : EditStopListItemUiEvent()
    data class Save(val action: () -> Unit) : EditStopListItemUiEvent()
    data class Delete(val action: () -> Unit) : EditStopListItemUiEvent()
}

class EditStopListItemViewModel(
    targetItem: StopListItem,
    private val stopListService: StopListService
) : ScreenModel {
    private val log = Logger.withTag("EditStopListItemViewModel")
    private val coroutineScope = screenModelScope

    private val _screenState = MutableStateFlow<EditStopListItemScreenState>(
        EditStopListItemScreenState.Main(originalItem = targetItem)
    )
    val screenState: StateFlow<EditStopListItemScreenState> = _screenState.asStateFlow()

    fun sendEvent(event: EditStopListItemUiEvent) {
        when (event) {
            is EditStopListItemUiEvent.SetRemainCount -> setRemainCount(count = event.count)
            is EditStopListItemUiEvent.SetUntil -> setUntil(value = event.value)
            is EditStopListItemUiEvent.Save -> saveItem(event.action)
            is EditStopListItemUiEvent.Delete -> deleteItem(event.action)
        }
    }

    private fun setRemainCount(count: Int) {
        if (count >= 0) transformMainState { it.copy(newRemainCount = count) }
    }

    private fun setUntil(value: LocalDateTime?) {
        transformMainState {
            it.copy(newUntil = value?.takeIf { date -> date > LocalDateTime.now() })
        }
    }

    private fun saveItem(action: () -> Unit) {
        withMainState()?.apply {
            coroutineScope.launch {
                setSaving(true)
                stopListService.editItem(
                    id = originalItem.id,
                    remainingCount = newRemainCount,
                    until = newUntil
                ).onSuccess { action() }
                setSaving(false)
            }
        }
    }

    private fun deleteItem(action: () -> Unit) {
        withMainState()?.apply {
            coroutineScope.launch {
                setSaving(true)
                stopListService.removeItem(id = originalItem.id).onSuccess { action() }
                setSaving(false)
            }
        }
    }

    private fun setSaving(value: Boolean) = transformMainState { it.copy(isSaving = value) }

    private fun transformMainState(
        action: (state: EditStopListItemScreenState.Main) -> EditStopListItemScreenState
    ) {
        val currentState = _screenState.value
        if (currentState is EditStopListItemScreenState.Main) {
            _screenState.value = action(currentState)
        }
    }

    private fun withMainState(): EditStopListItemScreenState.Main? {
        val state = _screenState.value
        return if (state is EditStopListItemScreenState.Main) state else null
    }
}