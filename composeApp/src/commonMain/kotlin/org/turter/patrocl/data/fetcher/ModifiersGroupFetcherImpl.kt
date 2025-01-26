package org.turter.patrocl.data.fetcher

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
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
import org.turter.patrocl.data.local.entity.ModifiersGroupLocal
import org.turter.patrocl.data.mapper.menu.toModifiersGroup
import org.turter.patrocl.data.mapper.menu.toModifiersGroupLocal
import org.turter.patrocl.data.remote.client.SourceApiClient
import org.turter.patrocl.domain.exception.EmptyMenuDataCategoryException
import org.turter.patrocl.domain.fetcher.ModifiersGroupFetcher
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.DataStatus.Empty
import org.turter.patrocl.domain.model.DataStatus.Initial
import org.turter.patrocl.domain.model.DataStatus.Loading
import org.turter.patrocl.domain.model.DataStatus.Ready
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.menu.ModifiersGroup

class ModifiersGroupFetcherImpl(
    private val sourceApiClient: SourceApiClient,
    private val localSource: LocalSource<ModifiersGroupLocal>
): ModifiersGroupFetcher {
    private val log = Logger.withTag("ModifiersGroupFetcherImpl")

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val modifiersGroupFlow = localSource
        .get()
        .map { res ->
            res.map { it.toModifiersGroup() }
        }
        .distinctUntilChanged()

    private val refreshModifiersGroupFlow = MutableSharedFlow<Unit>(replay = 1)

    private val modifiersGroupDataStatus = MutableStateFlow<DataStatus>(Initial)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val modifiersGroupTreeStateFlow = flow<FetchState<ModifiersGroup>> {
        log.d { "Creating modifiers group tree state flow" }
        refreshModifiersGroupFlow.emit(Unit)
        refreshModifiersGroupFlow.collect {
            log.d { "Modifiers group tree state flow - collect event" }
            emit(FetchState.loading())
            modifiersGroupDataStatus.emit(Loading)

            modifiersGroupFlow.flatMapLatest { current ->
                log.d { "Modifiers group tree state flow - latest modifiers group result: $current" }
                if (current.isSuccess) {
                    log.d { "Modifiers group is present - emit current value" }
                    flowOf(current)
                } else {
                    flow<Result<ModifiersGroup>> {
                        log.d { "Modifiers group result is failure - start updating from remote" }
                        refreshFromRemote()
                        emitAll(modifiersGroupFlow)
                    }
                }
            }.collect { result ->
                result.fold(
                    onSuccess = {
                        emit(FetchState.done(result))
                        log.d { "Current modifiers group is present, emit data status READY" }
                        modifiersGroupDataStatus.emit(Ready)
                    },
                    onFailure = {
                        emit(FetchState.fail(EmptyMenuDataCategoryException()))
                        log.d { "Current modifiers group is null, emit data status EMPTY" }
                        modifiersGroupDataStatus.emit(Empty)
                    }
                )
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    override fun getStateFlow(): StateFlow<FetchState<ModifiersGroup>> =
        modifiersGroupTreeStateFlow

    override fun getDataStatus(): StateFlow<DataStatus> = modifiersGroupDataStatus.asStateFlow()

    override suspend fun refresh() {
        refreshModifiersGroupFlow.emit(Unit)
    }

    override suspend fun refreshFromRemote() {
        log.d { "Start updating modifiers group from remote" }
        modifiersGroupDataStatus.emit(Loading)
        sourceApiClient.getModifiersGroupTree().fold(
            onSuccess = { dto ->
                log.d { "Success fetching modifiers group from remote - start replace to local data. " +
                        "ModifiersGroupDto: $dto" }
                localSource.replace(dto.toModifiersGroupLocal())
                modifiersGroupDataStatus.emit(Ready)
            },
            onFailure = { cause ->
                log.e { "Fail fetching modifiers group from remote - start cleanup local data" }
                localSource.cleanUp()
                modifiersGroupDataStatus.emit(Empty)
            }
        )
    }
}