package org.turter.patrocl.presentation.stoplist.create

import kotlinx.datetime.LocalDateTime
import org.turter.patrocl.domain.model.menu.Dish
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.presentation.error.ErrorType

sealed class CreateStopListItemScreenState {
    data object Initial: CreateStopListItemScreenState()

    data object Loading: CreateStopListItemScreenState()

    data class Main(
        val items: List<StopListItem>,
        val dishes: List<Dish>,
        val selectedDishId: String = "",
        val remainCount: Int = 0,
        val until: LocalDateTime? = null,
        val isCreating: Boolean = false
    ) : CreateStopListItemScreenState() {
        fun isDishNotInStopList(dishId: String) = items.none { it.dishId == dishId }
        fun getSelectedDish() = dishes.find { it.id == selectedDishId }
    }

    data class Error(
        val errorType: ErrorType
    ) : CreateStopListItemScreenState()
}