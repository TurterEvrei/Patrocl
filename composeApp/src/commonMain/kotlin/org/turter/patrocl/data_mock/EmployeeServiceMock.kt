package org.turter.patrocl.data_mock

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.turter.patrocl.data_mock.utils.EmployeeDataSupplier
import org.turter.patrocl.domain.model.BindStatus
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.person.Employee
import org.turter.patrocl.domain.service.EmployeeService

class EmployeeServiceMock : EmployeeService{
    private val DEFAULT_EMPLOYEE = EmployeeDataSupplier.getEmployee()

    private val _employeeStateFlow = MutableStateFlow(FetchState.success(DEFAULT_EMPLOYEE))
    private val _bindStatusStateFlow = MutableStateFlow<BindStatus>(BindStatus.Bind)

    override fun getOwnEmployeeStateFlow(): StateFlow<FetchState<Employee>> =
        _employeeStateFlow.asStateFlow()

    override fun getOwnEmployeeBindStatusStateFlow(): StateFlow<BindStatus> =
        _bindStatusStateFlow.asStateFlow()

    override suspend fun checkEmployee() {
        _bindStatusStateFlow.value = BindStatus.Loading
        _employeeStateFlow.value = FetchState.loading()
        delay(1000)
        _bindStatusStateFlow.value = BindStatus.Bind
        _employeeStateFlow.value = FetchState.success(DEFAULT_EMPLOYEE)
    }

    override suspend fun updateEmployeeFromRemote(): Result<Employee> {
        delay(1000)
        _bindStatusStateFlow.value = BindStatus.Bind
        _employeeStateFlow.value = FetchState.success(DEFAULT_EMPLOYEE)
        return Result.success(DEFAULT_EMPLOYEE)
    }

    override suspend fun changePreferCompany(preferCompanyId: String): Result<Unit> {
        delay(1000)
        _employeeStateFlow.value = FetchState.success(
            DEFAULT_EMPLOYEE.copy(preferredCompanyId = preferCompanyId)
        )
        return Result.success(Unit)
    }


}