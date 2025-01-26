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
import org.turter.patrocl.data.local.entity.DishModifierLocal
import org.turter.patrocl.data.mapper.menu.toModifierListFromLocal
import org.turter.patrocl.data.mapper.menu.toModifierLocalList
import org.turter.patrocl.data.remote.client.SourceApiClient
import org.turter.patrocl.domain.exception.EmptyMenuDataModifiersException
import org.turter.patrocl.domain.fetcher.ModifiersFetcher
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.DataStatus.Empty
import org.turter.patrocl.domain.model.DataStatus.Initial
import org.turter.patrocl.domain.model.DataStatus.Loading
import org.turter.patrocl.domain.model.DataStatus.Ready
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.menu.DishModifier

class ModifiersFetcherImpl(
    private val sourceApiClient: SourceApiClient,
    private val modifiersLocalSource: LocalSource<List<DishModifierLocal>>
) : ModifiersFetcher {
    private val log = Logger.withTag("ModifiersFetcherImpl")

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val modifiersFlow = modifiersLocalSource
        .get()
        .map { res ->
            res.map { it.toModifierListFromLocal() }
        }
        .distinctUntilChanged()

    private val refreshModifiersFlow = MutableSharedFlow<Unit>(replay = 1)

    private val modifiersDataStatus = MutableStateFlow<DataStatus>(Initial)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val modifiersStateFlow = flow<FetchState<List<DishModifier>>> {
        log.d { "Creating modifiers state flow" }
        refreshModifiersFlow.emit(Unit)
        refreshModifiersFlow.collect {
            log.d { "Modifiers state flow - collect event" }
            emit(FetchState.loading())
            modifiersDataStatus.emit(Loading)

            modifiersFlow.flatMapLatest { current ->
                log.d { "Modifiers state flow - latest modifiers list result: $current" }
                if (current.isSuccess) {
                    log.d { "Modifiers is present - emit current value" }
                    flowOf(current)
                } else {
                    flow<Result<List<DishModifier>>> {
                        log.d { "Modifiers result is failure - start updating from remote" }
                        refreshFromRemote()
                        emitAll(modifiersFlow)
                    }
                }
            }.collect { result ->
                result.fold(
                    onSuccess = {
                        emit(FetchState.done(result))
                        log.d { "Current modifiers is present, emit data status READY" }
                        modifiersDataStatus.emit(Ready)
                    },
                    onFailure = {
                        emit(FetchState.fail(EmptyMenuDataModifiersException()))
                        log.d { "Current modifiers is empty, emit data status EMPTY" }
                        modifiersDataStatus.emit(Empty)
                    }
                )
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    override fun getStateFlow(): StateFlow<FetchState<List<DishModifier>>> = modifiersStateFlow

    override fun getDataStatus(): StateFlow<DataStatus> = modifiersDataStatus.asStateFlow()

    override suspend fun refresh() {
        refreshModifiersFlow.emit(Unit)
    }

    override suspend fun refreshFromRemote() {
        log.d { "Start updating modifiers from remote" }
        modifiersDataStatus.emit(Loading)
        sourceApiClient.getModifiers().fold(
            onSuccess = { modifiersList ->
                log.d { "Success fetching modifiers from remote - start replace to local data. " +
                        "Modifiers list size: ${modifiersList.size}" }
                modifiersLocalSource.replace(modifiersList.toModifierLocalList())
                modifiersDataStatus.emit(Ready)
            },
            onFailure = { cause ->
                log.e { "Fail fetching modifiers from remote - start cleanup local data" }
                modifiersLocalSource.cleanUp()
                modifiersDataStatus.emit(Empty)
            }
        )
    }
}