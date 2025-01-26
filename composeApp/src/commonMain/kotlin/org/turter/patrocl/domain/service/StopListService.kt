package org.turter.patrocl.domain.service

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDateTime
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.stoplist.NewStopListItem
import org.turter.patrocl.domain.model.stoplist.StopList
import org.turter.patrocl.domain.model.stoplist.StopListItem

interface StopListService {

    fun getStopListStateFlow(): StateFlow<FetchState<StopList.Success>>

    suspend fun refreshStopList()

    suspend fun createNewItem(item: NewStopListItem): Result<Unit>

    suspend fun editItem(id: String, remainingCount: Int, until: LocalDateTime?): Result<Unit>

    suspend fun removeItem(id: String): Result<Unit>

    suspend fun removeItems(ids: List<String>): Result<Unit>

}