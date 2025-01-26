package org.turter.patrocl.data.local

import kotlinx.coroutines.flow.Flow

interface LocalSource<T> {

    fun get(): Flow<Result<T>>

    suspend fun replace(data: T)

    suspend fun cleanUp()
}