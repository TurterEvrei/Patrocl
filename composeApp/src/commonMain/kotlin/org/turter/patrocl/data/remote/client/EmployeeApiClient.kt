package org.turter.patrocl.data.remote.client

import org.turter.patrocl.data.dto.person.EditOwnEmployeePayload
import org.turter.patrocl.data.dto.person.EmployeeDto

interface EmployeeApiClient {
    suspend fun getOwnEmployee(): Result<EmployeeDto>
    suspend fun editOwnEmployee(payload: EditOwnEmployeePayload): Result<Unit>
}