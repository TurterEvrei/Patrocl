package org.turter.patrocl.data.service

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.turter.patrocl.data.mapper.toWaiterFromLocal
import org.turter.patrocl.data.mapper.toWaiterLocalFromDto
import org.turter.patrocl.domain.BindStatus
import org.turter.patrocl.domain.client.WaiterApiClient
import org.turter.patrocl.domain.dto.EditOwnWaiterPayload
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.domain.repository.WaiterLocalRepository
import org.turter.patrocl.domain.service.WaiterService

class WaiterServiceImpl(
    private val waiterApiClient: WaiterApiClient,
    private val waiterLocalRepository: WaiterLocalRepository
) : WaiterService {
    private val log = Logger.withTag("WaiterServiceImpl")

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val waiterFlow = waiterLocalRepository
        .get()
        .map { res -> res.map { it.toWaiterFromLocal() } }
        .distinctUntilChanged()

    private val refreshWaiterFlow = MutableSharedFlow<Unit>(replay = 1)

    private val waiterDataStatusFlow = MutableStateFlow<BindStatus>(BindStatus.Initial)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val waiterStateFlow = flow<FetchState<Waiter>> {
        log.d { "Start waiter state flow" }
        refreshWaiterFlow.emit(Unit)
        refreshWaiterFlow.collect {
            log.d { "Collecting waiter refresh event" }
            emit(FetchState.loading())
            waiterDataStatusFlow.emit(BindStatus.Loading)

            waiterFlow.flatMapLatest { current ->
                log.d { "Current waiter result: $current" }
                if (current.isSuccess) {
                    log.d { "Current waiter result is success - emit current value" }
                    flowOf(current)
                } else {
                    flow<Result<Waiter>> {
                        log.d { "Current waiter result is failure - start updating from remote " }
                        updateWaiterFromRemote()
                        emitAll(waiterFlow)
                    }
                }
            }.collect { result ->
                result.fold(
                    onSuccess = { value ->
                        emit(FetchState.success(value))
                        log.d { "Waiter result is success - emit bind status BIND" }
                        waiterDataStatusFlow.emit(BindStatus.Bind)
                    },
                    onFailure = { cause ->
                        emit(FetchState.fail(cause))
                        log.d { "Current waiter not present - emit bind status NOT_BIND" }
                        waiterDataStatusFlow.emit(BindStatus.NotBind)
                    }
                )
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    override fun getOwnWaiterStateFlow(): StateFlow<FetchState<Waiter>> =
        waiterStateFlow

    override fun getOwnWaiterBindStatus(): StateFlow<BindStatus> =
        waiterDataStatusFlow.asStateFlow()

    override suspend fun checkWaiter() {
        log.d { "Check waiter" }
        refreshWaiterFlow.emit(Unit)
    }

    override suspend fun updateWaiterFromRemote() {
        log.d { "Start updating waiter from remote" }
        waiterApiClient.getOwnWaiter().fold(
            onSuccess = { waiterDto ->
                log.d {
                    "Success fetching waiter from remote - start replace to local data. " +
                            "WaiterDto: $waiterDto"
                }
                waiterLocalRepository.replace(waiterDto.toWaiterLocalFromDto())
            },
            onFailure = { cause ->
                log.e { "Fail fetching waiter from remote - start cleanup local data" }
                waiterLocalRepository.cleanUp()
            }
        )
    }

    override suspend fun changePreferCompany(preferCompanyId: String): Result<Unit> =
        waiterApiClient.editOwnWaiter(
            EditOwnWaiterPayload(preferCompanyId = preferCompanyId)
        ).apply {
            waiterLocalRepository.update { current ->
                current.apply { this.preferCompanyId = preferCompanyId }
            }
        }
}