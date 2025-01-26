package org.turter.patrocl.data.remote.client

import org.turter.patrocl.data.dto.person.WaiterDto

interface WaiterApiClient {
    suspend fun getOwnWaiter(): Result<WaiterDto>
}