package org.turter.patrocl.data.remote.client

import org.turter.patrocl.data.dto.source.TableDto

interface TableApiClient {
    suspend fun getTables(): Result<List<TableDto>>
}