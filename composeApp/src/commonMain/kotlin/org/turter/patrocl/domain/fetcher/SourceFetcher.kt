package org.turter.patrocl.domain.fetcher

import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.FetchState

interface SourceFetcher<T> {
    fun getStateFlow(): StateFlow<FetchState<T>>
    fun getDataStatus(): StateFlow<DataStatus>
    suspend fun refresh()
    suspend fun refreshFromRemote()
}