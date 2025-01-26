package org.turter.patrocl.presentation.stoplist.edit

import kotlinx.datetime.LocalDateTime
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.presentation.error.ErrorType

sealed class EditStopListItemScreenState {
    data object Loading: EditStopListItemScreenState()

    data class Main(
        val originalItem: StopListItem,
        val newRemainCount: Int = originalItem.remainingCount,
        val newUntil: LocalDateTime? = originalItem.until,
        val isSaving: Boolean = false
    ) : EditStopListItemScreenState()

//    data class Error(
//        val errorType: ErrorType
//    ) : EditStopListItemScreenState()
}