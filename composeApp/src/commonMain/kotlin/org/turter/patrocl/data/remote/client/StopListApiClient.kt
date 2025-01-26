package org.turter.patrocl.data.remote.client

import kotlinx.coroutines.flow.Flow
import org.turter.patrocl.data.dto.stoplist.CreateStopListItemPayload
import org.turter.patrocl.data.dto.stoplist.EditStopListItemPayload
import org.turter.patrocl.data.dto.stoplist.StopListDto
import org.turter.patrocl.data.dto.stoplist.StopListItemDto

interface StopListApiClient {
    suspend fun getStopList(): Result<List<StopListItemDto>>
    suspend fun getStopListFlow(): Flow<Result<StopListDto>>
    suspend fun createItem(payload: CreateStopListItemPayload): Result<StopListItemDto>
    suspend fun editItem(payload: EditStopListItemPayload): Result<StopListItemDto>
    suspend fun deleteItem(itemId: String): Result<Unit>
    suspend fun deleteItems(ids: List<String>): Result<Unit>
}