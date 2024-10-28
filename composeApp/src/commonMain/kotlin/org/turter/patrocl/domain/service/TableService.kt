package org.turter.patrocl.domain.service

import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.source.Table

interface TableService {
    fun getTablesStateFlow(): StateFlow<FetchState<List<Table>>>
    fun getTablesDataStatusStateFlow(): StateFlow<DataStatus>
    suspend fun refreshTables()
    suspend fun refreshTablesFromApi()
}