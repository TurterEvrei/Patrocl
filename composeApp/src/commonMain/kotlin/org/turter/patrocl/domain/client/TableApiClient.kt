package org.turter.patrocl.domain.client

import org.turter.patrocl.domain.dto.TableDto

interface TableApiClient {
    suspend fun getTables(): Result<List<TableDto>>
}