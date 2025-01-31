package org.turter.patrocl.domain.service

import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.domain.model.BindStatus
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.person.Employee

interface EmployeeService {
    fun getOwnEmployeeStateFlow(): StateFlow<FetchState<Employee>>
    fun getOwnEmployeeBindStatusStateFlow(): StateFlow<BindStatus>
    suspend fun checkEmployee()
    suspend fun updateEmployeeFromRemote(): Result<Employee>
    suspend fun changePreferCompany(preferCompanyId: String): Result<Unit>
}