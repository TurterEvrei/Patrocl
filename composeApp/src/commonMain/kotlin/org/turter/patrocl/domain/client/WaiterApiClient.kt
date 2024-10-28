package org.turter.patrocl.domain.client

import org.turter.patrocl.domain.dto.EditOwnWaiterPayload
import org.turter.patrocl.domain.dto.WaiterDto

interface WaiterApiClient {
    suspend fun getOwnWaiter(): Result<WaiterDto>

    suspend fun editOwnWaiter(
        payload: EditOwnWaiterPayload
    ): Result<Unit>
}