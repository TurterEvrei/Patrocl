package org.turter.patrocl.presentation.stoplist.list

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.presentation.error.ErrorType

sealed class StopListScreenState {
    data object Initial: StopListScreenState()

    data object Loading: StopListScreenState()

    data class Main(
        val items: List<StopListItem>,
        val selectedItemsIds: SnapshotStateList<String> = mutableStateListOf(),
        val isRemoving: Boolean = false
    ) : StopListScreenState() {
        fun getSelectedItems() = items.filter { selectedItemsIds.contains(it.id) }.toList()
        fun isNoSelectedItems() = selectedItemsIds.isEmpty()
    }

    data class Error(
        val errorType: ErrorType
    ) : StopListScreenState()
}