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
import org.turter.patrocl.data.local.entity.CategoryLocal
import org.turter.patrocl.data.mapper.menu.toCategory
import org.turter.patrocl.data.mapper.menu.toCategoryLocal
import org.turter.patrocl.data.remote.client.SourceApiClient
import org.turter.patrocl.domain.exception.EmptyMenuDataCategoryException
import org.turter.patrocl.domain.fetcher.CategoryFetcher
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.DataStatus.Empty
import org.turter.patrocl.domain.model.DataStatus.Initial
import org.turter.patrocl.domain.model.DataStatus.Loading
import org.turter.patrocl.domain.model.DataStatus.Ready
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.menu.Category

class CategoryFetcherImpl(
    private val sourceApiClient: SourceApiClient,
    private val categoryLocalSource: LocalSource<CategoryLocal>
): CategoryFetcher {
    private val log = Logger.withTag("CategoryFetcherImpl")

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val categoryFlow = categoryLocalSource
        .get()
        .map { res ->
            res.map { it.toCategory() }
        }
        .distinctUntilChanged()

    private val refreshCategoryFlow = MutableSharedFlow<Unit>(replay = 1)

    private val categoryDataStatus = MutableStateFlow<DataStatus>(Initial)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val categoryTreeStateFlow = flow<FetchState<Category>> {
        log.d { "Creating category tree state flow" }
        refreshCategoryFlow.emit(Unit)
        refreshCategoryFlow.collect {
            log.d { "Category tree state flow - collect event" }
            emit(FetchState.loading())
            categoryDataStatus.emit(Loading)

            categoryFlow.flatMapLatest { current ->
                log.d { "Category tree state flow - latest category result: $current" }
                if (current.isSuccess) {
                    log.d { "Category is present - emit current value" }
                    flowOf(current)
                } else {
                    flow<Result<Category>> {
                        log.d { "Category result is failure - start updating from remote" }
                        refreshFromRemote()
                        emitAll(categoryFlow)
                    }
                }
            }.collect { result ->
                result.fold(
                    onSuccess = {
                        emit(FetchState.done(result))
                        log.d { "Current category is present, emit data status READY" }
                        categoryDataStatus.emit(Ready)
                    },
                    onFailure = {
                        emit(FetchState.fail(EmptyMenuDataCategoryException()))
                        log.d { "Current category is null, emit data status EMPTY" }
                        categoryDataStatus.emit(Empty)
                    }
                )
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    override fun getStateFlow(): StateFlow<FetchState<Category>> = categoryTreeStateFlow

    override fun getDataStatus(): StateFlow<DataStatus> = categoryDataStatus.asStateFlow()

    override suspend fun refresh() {
        refreshCategoryFlow.emit(Unit)
    }

    override suspend fun refreshFromRemote() {
        log.d { "Start updating category from remote" }
        categoryDataStatus.emit(Loading)
        sourceApiClient.getCategoryTree().fold(
            onSuccess = { categoryDto ->
                log.d { "Success fetching category from remote - start replace to local data. " +
                        "CategoryDto: $categoryDto" }
                categoryLocalSource.replace(categoryDto.toCategoryLocal())
                categoryDataStatus.emit(Ready)
            },
            onFailure = { cause ->
                log.e { "Fail fetching category from remote - start cleanup local data" }
                categoryLocalSource.cleanUp()
                categoryDataStatus.emit(Empty)
            }
        )
    }

}