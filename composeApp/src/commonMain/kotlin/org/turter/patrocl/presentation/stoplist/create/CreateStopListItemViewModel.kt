package org.turter.patrocl.presentation.stoplist.create

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.turter.patrocl.domain.fetcher.DishFetcher
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.stoplist.NewStopListItem
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.domain.service.StopListService
import org.turter.patrocl.presentation.error.ErrorType
import org.turter.patrocl.utils.now

sealed class CreateStopListItemUiEvent {
    data object BackToList : CreateStopListItemUiEvent()
    data class SelectDish(val id: String) : CreateStopListItemUiEvent()
    data class SetRemainCount(val count: Int) : CreateStopListItemUiEvent()
    data class SetUntil(val value: LocalDateTime?) : CreateStopListItemUiEvent()
    data object Create : CreateStopListItemUiEvent()
    data object Refresh : CreateStopListItemUiEvent()
}

class CreateStopListItemViewModel(
    private val currentStopList: List<StopListItem>,
    private val navigator: Navigator,
    private val dishFetcher: DishFetcher,
    private val stopListService: StopListService
) : ScreenModel {
    private val log = Logger.withTag("CreateStopListItemViewModel")
    private val coroutineScope = screenModelScope

    private val _screenState =
        MutableStateFlow<CreateStopListItemScreenState>(CreateStopListItemScreenState.Initial)
    val screenState: StateFlow<CreateStopListItemScreenState> = _screenState.asStateFlow()

    init {
        coroutineScope.launch {
            dishFetcher.getStateFlow().collect { fetchState ->
                _screenState.value = when (fetchState) {
                    is FetchState.Finished -> fetchState.result.fold(
                        onSuccess = {
                            CreateStopListItemScreenState.Main(items = currentStopList, dishes = it)
                        },
                        onFailure = { CreateStopListItemScreenState.Error(ErrorType.from(it)) }
                    )

                    else -> CreateStopListItemScreenState.Loading
                }
            }
        }
    }

    fun sendEvent(event: CreateStopListItemUiEvent) {
        when (event) {
            is CreateStopListItemUiEvent.BackToList -> backToStopList()
            is CreateStopListItemUiEvent.SelectDish -> selectDish(id = event.id)
            is CreateStopListItemUiEvent.SetRemainCount -> setRemainCount(count = event.count)
            is CreateStopListItemUiEvent.SetUntil -> setUntil(value = event.value)
            is CreateStopListItemUiEvent.Create -> createItem()
            is CreateStopListItemUiEvent.Refresh -> refresh()
        }
    }

    private fun backToStopList() {
        navigator.pop()
    }

    private fun selectDish(id: String) {
        transformMainState { it.copy(selectedDishId = id) }
    }

    private fun setRemainCount(count: Int) {
        if (count >= 0) transformMainState { it.copy(remainCount = count) }
    }

    private fun setUntil(value: LocalDateTime?) {
        transformMainState { it.copy(until = value?.takeIf { date -> date > LocalDateTime.now() }) }
    }

    private fun createItem() {
        withMainState()?.apply {
            if (isDishNotInStopList(selectedDishId) && dishes.any { it.id == selectedDishId })
                coroutineScope.launch {
                    setCreating(true)
                    stopListService.createNewItem(
                        item = NewStopListItem(
                            dishId = selectedDishId,
                            remainingCount = remainCount,
                            until = until
                        )
                    ).onSuccess { navigator.pop() }
                    setCreating(false)
                }
        }
    }

    private fun refresh() {
        coroutineScope.launch {
            dishFetcher.refresh()
        }
    }

    private fun setCreating(value: Boolean) = transformMainState { it.copy(isCreating = value) }

    private fun transformMainState(
        action: (state: CreateStopListItemScreenState.Main) -> CreateStopListItemScreenState
    ) {
        val currentState = _screenState.value
        if (currentState is CreateStopListItemScreenState.Main) {
            _screenState.value = action(currentState)
        }
    }

    private fun withMainState(): CreateStopListItemScreenState.Main? {
        val state = _screenState.value
        return if (state is CreateStopListItemScreenState.Main) state else null
    }
}