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
import org.turter.patrocl.data.local.LocalSource
import org.turter.patrocl.data.local.entity.TableLocal
import org.turter.patrocl.data.mapper.toTableListFromLocal
import org.turter.patrocl.data.mapper.toTableLocalList
import org.turter.patrocl.data.remote.client.TableApiClient
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.DataStatus.Empty
import org.turter.patrocl.domain.model.DataStatus.Loading
import org.turter.patrocl.domain.model.DataStatus.Ready
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.domain.service.TableService

class TableServiceImpl(
    private val tableApiClient: TableApiClient,
    private val tableLocalSource: LocalSource<List<TableLocal>>
): TableService {
    private val log = Logger.withTag("TableServiceImpl")

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val tablesFlow = tableLocalSource
        .get()
        .map { res -> res.map { it.toTableListFromLocal() } }
        .distinctUntilChanged()

    private val refreshTableFlow = MutableSharedFlow<Unit>(replay = 1)

    private val tableDataStatusFlow = MutableStateFlow<DataStatus>(DataStatus.Initial)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val tablesStateFlow = flow<FetchState<List<Table>>> {
        log.d { "Start tables state flow" }
        refreshTableFlow.emit(Unit)
        refreshTableFlow.collect {
            log.d { "Collecting table refresh event" }
            emit(FetchState.loading())
            tableDataStatusFlow.emit(Loading)

            tablesFlow.flatMapLatest { current ->
                log.d { "Current tables result: $current" }
                if (current.isSuccess) {
                    log.d { "Current tables result is success - emit current value" }
                    flowOf(current)
                } else {
                    flow<Result<List<Table>>> {
                        log.d { "Current tables result is failure - start updating from remote " }
                        updateTablesFromRemote()
                        emitAll(tablesFlow)
                    }
                }
            }.collect { result ->
                result.fold(
                    onSuccess = { value ->
                        emit(FetchState.success(value))
                        log.d { "Tables result is success - emit data status READY" }
                        tableDataStatusFlow.emit(Ready)
                    },
                    onFailure = { cause ->
                        emit(FetchState.fail(cause))
                        log.d { "Current tables not present - emit data status EMPTY" }
                        tableDataStatusFlow.emit(Empty)
                    }
                )
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    override fun getTablesStateFlow(): StateFlow<FetchState<List<Table>>> =
        tablesStateFlow

    override fun getTablesDataStatusStateFlow(): StateFlow<DataStatus> =
        tableDataStatusFlow.asStateFlow()

    override suspend fun refreshTables() =
        refreshTableFlow.emit(Unit)

    override suspend fun refreshTablesFromApi() = updateTablesFromRemote()

    private suspend fun updateTablesFromRemote() {
        log.d { "Start updating tables from remote" }
        tableDataStatusFlow.emit(Loading)
        tableApiClient.getTables().fold(
            onSuccess = { tableList ->
                log.d { "Success fetching tables from remote - start replace to local data. " +
                        "Tables list size: ${tableList.size}" }
                tableLocalSource.replace(tableList.toTableLocalList())
                tableDataStatusFlow.emit(Ready)
            },
            onFailure = { cause ->
                log.e { "Fail fetching tables from remote - start cleanup local data" }
                tableLocalSource.cleanUp()
                tableDataStatusFlow.emit(Empty)
            }
        )
    }
}