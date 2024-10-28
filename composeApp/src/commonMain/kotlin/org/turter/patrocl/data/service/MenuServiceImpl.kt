package org.turter.patrocl.data.service

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.repository.LocalSource
import org.turter.patrocl.data.mapper.toCategory
import org.turter.patrocl.data.mapper.toCategoryLocal
import org.turter.patrocl.data.mapper.toDishListFromLocal
import org.turter.patrocl.data.mapper.toDishLocalList
import org.turter.patrocl.data.mapper.toModifierListFromLocal
import org.turter.patrocl.data.mapper.toModifierLocalList
import org.turter.patrocl.domain.client.MenuApiClient
import org.turter.patrocl.domain.entity.CategoryLocal
import org.turter.patrocl.domain.entity.DishLocal
import org.turter.patrocl.domain.entity.DishModifierLocal
import org.turter.patrocl.domain.exception.ComponentMenuErrorException
import org.turter.patrocl.domain.exception.EmptyMenuDataCategoryException
import org.turter.patrocl.domain.exception.EmptyMenuDataDishesException
import org.turter.patrocl.domain.exception.EmptyMenuDataModifiersException
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.DataStatus.*
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.FetchState.Finished
import org.turter.patrocl.domain.model.source.Category
import org.turter.patrocl.domain.model.source.Dish
import org.turter.patrocl.domain.model.source.DishModifier
import org.turter.patrocl.domain.model.source.MenuData
import org.turter.patrocl.domain.service.MenuService

class MenuServiceImpl(
    private val menuApiClient: MenuApiClient,
    private val categoryLocalSource: LocalSource<CategoryLocal>,
    private val dishLocalSource: LocalSource<List<DishLocal>>,
    private val modifiersLocalSource: LocalSource<List<DishModifierLocal>>,
) : MenuService {
    private val log = Logger.withTag("MenuServiceImpl")

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val categoryFlow = categoryLocalSource
        .get()
        .map { res ->
            res.map { it.toCategory() }
        }
        .distinctUntilChanged()

    private val dishesFlow = dishLocalSource
        .get()
        .map { res ->
            res.map { it.toDishListFromLocal() }
        }
        .distinctUntilChanged()


    private val modifiersFlow = modifiersLocalSource
        .get()
        .map { res ->
            res.map { it.toModifierListFromLocal() }
        }
        .distinctUntilChanged()

    private val refreshCategoryFlow = MutableSharedFlow<Unit>(replay = 1)
    private val refreshDishesFlow = MutableSharedFlow<Unit>(replay = 1)
    private val refreshModifiersFlow = MutableSharedFlow<Unit>(replay = 1)

    private val categoryDataStatus = MutableStateFlow<DataStatus>(Initial)
    private val dishDataStatus = MutableStateFlow<DataStatus>(Initial)
    private val modifiersDataStatus = MutableStateFlow<DataStatus>(Initial)

    private val menuDataStatus = flow<DataStatus> {
        combine(categoryDataStatus, dishDataStatus, modifiersDataStatus) { cat, dishes, modis ->
            if (cat is Ready && dishes is Ready && modis is Ready) Ready
            else if (cat is Error || dishes is Error || modis is Error) Error(
                ComponentMenuErrorException()
            )
            else if (cat is Empty || dishes is Empty || modis is Empty) Empty
            else if (cat is Initial || dishes is Initial || modis is Initial) Initial
            else Loading
        }.collect { newValue ->
            emit(newValue)
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = Initial
    )

    private val menuDataStateFlow = flow<FetchState<MenuData>> {
        combine(
            categoryTreeStateFlow,
            dishesStateFlow,
            modifiersStateFlow
        ) { category, dishes, modifiers ->
            log.d { "Combine flows to menu data flow" }
            if (category is Finished && dishes is Finished && modifiers is Finished) {
                try {
                    FetchState.success(
                        MenuData(
                            category = category.result.getOrThrow(),
                            dishes = dishes.result.getOrThrow(),
                            modifiers = modifiers.result.getOrThrow()
                        )
                    )
                } catch (e: Exception) {
                    log.e { "Catch exception in results of flows. Exception: $e" }
                    e.printStackTrace()
                    FetchState.fail(e)
                }
            } else {
                FetchState.loading()
            }
        }.collect { fetchState ->
            emit(fetchState)
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val categoryTreeStateFlow = flow<FetchState<Category>> {
        log.d { "Creating menu tree state flow" }
        refreshCategoryFlow.emit(Unit)
        refreshCategoryFlow.collect {
            log.d { "Menu tree state flow - collect event" }
            emit(FetchState.loading())
            categoryDataStatus.emit(Loading)

            categoryFlow.flatMapLatest { current ->
                log.d { "Menu tree state flow - latest category result: $current" }
                if (current.isSuccess) {
                    log.d { "Category is present - emit current value" }
                    flowOf(current)
                } else {
                    flow<Result<Category>> {
                        log.d { "Category result is failure - start updating from remote" }
                        updateCategoryFromRemote()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dishesStateFlow = flow<FetchState<List<Dish>>> {
        log.d { "Creating dishes state flow" }
        refreshDishesFlow.emit(Unit)
        refreshDishesFlow.collect {
            log.d { "Dishes state flow - collect event" }
            emit(FetchState.loading())
            dishDataStatus.emit(Loading)

            dishesFlow.flatMapLatest { current ->
                log.d { "Dishes state flow - latest dish list result: $current" }
                if (current.isSuccess) {
                    log.d { "Dishes is present - emit current value" }
                    flowOf(current)
                } else {
                    flow<Result<List<Dish>>> {
                        log.d { "Dishes result is failure - start updating from remote" }
                        updateDishesFromRemote()
                        emitAll(dishesFlow)
                    }
                }
            }.collect { result ->
                result.fold(
                    onSuccess = {
                        emit(FetchState.done(result))
                        log.d { "Current dishes is present, emit data status READY" }
                        dishDataStatus.emit(Ready)
                    },
                    onFailure = {
                        emit(FetchState.fail(EmptyMenuDataDishesException()))
                        log.d { "Current dishes is empty, emit data status EMPTY" }
                        dishDataStatus.emit(Empty)
                    }
                )
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

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
                        updateModifiersFromRemote()
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

    override fun getCategoryTreeStateFlow(): StateFlow<FetchState<Category>> =
        categoryTreeStateFlow

    override fun getDishesStateFlow(): StateFlow<FetchState<List<Dish>>> =
        dishesStateFlow

    override fun getModifiersStateFlow(): StateFlow<FetchState<List<DishModifier>>> =
        modifiersStateFlow

    override fun getMenuDataStateFlow(): StateFlow<FetchState<MenuData>> =
        menuDataStateFlow

    override fun getCategoryTreeDataStatusStateFlow(): StateFlow<DataStatus> =
        categoryDataStatus.asStateFlow()

    override fun getDishesDataStatusStateFlow(): StateFlow<DataStatus> =
        dishDataStatus.asStateFlow()

    override fun getModifiersDataStatusStateFlow(): StateFlow<DataStatus> =
        modifiersDataStatus.asStateFlow()

    override fun getMenuDataStatusStateFlow(): StateFlow<DataStatus> =
        menuDataStatus

    override suspend fun refreshMenu() {
        refreshCategoryFlow.emit(Unit)
        refreshDishesFlow.emit(Unit)
        refreshModifiersFlow.emit(Unit)
    }

    override suspend fun refreshMenuFromApi() {
        coroutineScope {
            awaitAll(
                async { updateCategoryFromRemote() },
                async { updateDishesFromRemote() },
                async { updateModifiersFromRemote() }
            )
        }
    }

    private suspend fun updateCategoryFromRemote() {
        log.d { "Start updating category from remote" }
        categoryDataStatus.emit(Loading)
        menuApiClient.getCategoryTree().fold(
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

    private suspend fun updateDishesFromRemote() {
        log.d { "Start updating dishes from remote" }
        dishDataStatus.emit(Loading)
        menuApiClient.getDishes().fold(
            onSuccess = { dishList ->
                log.d { "Success fetching dishes from remote - start replace to local data. " +
                        "Dish list size: ${dishList.size}" }
                dishLocalSource.replace(dishList.toDishLocalList())
                dishDataStatus.emit(Ready)
            },
            onFailure = { cause ->
                log.e { "Fail fetching dishes from remote - start cleanup local data" }
                dishLocalSource.cleanUp()
                dishDataStatus.emit(Empty)
            }
        )
    }

    private suspend fun updateModifiersFromRemote() {
        log.d { "Start updating modifiers from remote" }
        modifiersDataStatus.emit(Loading)
        menuApiClient.getModifiers().fold(
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