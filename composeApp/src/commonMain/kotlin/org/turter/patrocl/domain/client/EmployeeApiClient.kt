package org.turter.patrocl.domain.client

import org.turter.patrocl.domain.dto.EmployeeDto

interface EmployeeApiClient {
    suspend fun getOwnEmployee(): Result<EmployeeDto>
}