package org.turter.patrocl.data.service

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.turter.patrocl.data.mapper.toEmployeeFromLocal
import org.turter.patrocl.data.mapper.toEmployeeLocalFromDto
import org.turter.patrocl.domain.BindStatus
import org.turter.patrocl.domain.client.EmployeeApiClient
import org.turter.patrocl.domain.entity.EmployeeLocal
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.person.Employee
import org.turter.patrocl.domain.repository.LocalSource
import org.turter.patrocl.domain.service.EmployeeService

class EmployeeServiceImpl(
    private val employeeApiClient: EmployeeApiClient,
    private val employeeLocalSource: LocalSource<EmployeeLocal>
) : EmployeeService {
    private val log = Logger.withTag("EmployeeRepositoryImpl")

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val employeeFlow = employeeLocalSource
        .get()
        .map { res -> res.map { it.toEmployeeFromLocal() } }
        .distinctUntilChanged()

    private val refreshEmployeeFlow = MutableSharedFlow<Unit>(replay = 1)

    private val employeeDataStatusFlow = MutableStateFlow<BindStatus>(BindStatus.Initial)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val employeeStateFlow = flow<FetchState<Employee>> {
        log.d { "Start employee state flow" }
        //TODO пересмотреть подход и может быть избавиться от refreshEmployeeFlow
        refreshEmployeeFlow.emit(Unit)
        refreshEmployeeFlow.collect {
            log.d { "Collecting employee refresh event" }
            emit(FetchState.loading())
            employeeDataStatusFlow.emit(BindStatus.Loading)

            employeeFlow.flatMapLatest { current ->
                log.d { "Current employee result: $current" }
                if (current.isSuccess) {
                    log.d { "Current employee result is success - emit current value" }
                    flowOf(current)
                } else {
                    flow<Result<Employee>> {
                        log.d { "Current employee result is failure - start updating from remote " }
                        updateEmployeeFromRemote()
                        emitAll(employeeFlow)
                    }
                }
            }.collect { result ->
                result.fold(
                    onSuccess = { value ->
                        emit(FetchState.success(value))
                        log.d { "Employee result is success - emit bind status BIND" }
                        employeeDataStatusFlow.emit(BindStatus.Bind)
                    },
                    onFailure = { cause ->
                        emit(FetchState.fail(cause))
                        log.d { "Current employee not present - emit bind status NOT_BIND" }
                        employeeDataStatusFlow.emit(BindStatus.NotBind)
                    }
                )
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    override fun getOwnEmployeeStateFlow(): StateFlow<FetchState<Employee>> =
        employeeStateFlow

    override fun getOwnEmployeeBindStatusStateFlow(): StateFlow<BindStatus> =
        employeeDataStatusFlow

    override suspend fun checkEmployee() = refreshEmployeeFlow.emit(Unit)

    override suspend fun updateEmployeeFromRemote() {
        log.d { "Start updating employee from remote" }
        employeeApiClient.getOwnEmployee().fold(
            onSuccess = { employeeDto ->
                log.d {
                    "Success fetching employee from remote - start replace to local data. " +
                            "EmployeeDto: $employeeDto"
                }
                employeeLocalSource.replace(employeeDto.toEmployeeLocalFromDto())
            },
            onFailure = { cause ->
                log.e { "Fail fetching employee from remote - start cleanup local data" }
                employeeLocalSource.cleanUp()
            }
        )
    }
}