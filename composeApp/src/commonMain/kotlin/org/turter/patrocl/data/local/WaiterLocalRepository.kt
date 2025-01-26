package org.turter.patrocl.data.local

import kotlinx.coroutines.flow.Flow
import org.turter.patrocl.data.local.entity.WaiterLocal

interface WaiterLocalRepository {
    fun get(): Flow<Result<WaiterLocal>>

    suspend fun update(transform: (WaiterLocal) -> WaiterLocal): Result<Unit>

    suspend fun replace(data: WaiterLocal)

    suspend fun cleanUp()
}