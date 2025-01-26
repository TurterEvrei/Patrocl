package org.turter.patrocl.data.service

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import org.turter.patrocl.data.mapper.menu.toDetailed
import org.turter.patrocl.domain.exception.ComponentMenuErrorException
import org.turter.patrocl.domain.fetcher.CategoryFetcher
import org.turter.patrocl.domain.fetcher.DishFetcher
import org.turter.patrocl.domain.fetcher.ModifiersFetcher
import org.turter.patrocl.domain.fetcher.ModifiersGroupFetcher
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.DataStatus.Empty
import org.turter.patrocl.domain.model.DataStatus.Error
import org.turter.patrocl.domain.model.DataStatus.Initial
import org.turter.patrocl.domain.model.DataStatus.Loading
import org.turter.patrocl.domain.model.DataStatus.Ready
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.FetchState.Finished
import org.turter.patrocl.domain.model.menu.Category
import org.turter.patrocl.domain.model.menu.Dish
import org.turter.patrocl.domain.model.menu.DishModifier
import org.turter.patrocl.domain.model.menu.MenuData
import org.turter.patrocl.domain.model.menu.ModifiersGroup
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.domain.service.MenuService
import org.turter.patrocl.domain.service.StopListService

class MenuServiceImpl(
    private val categoryFetcher: CategoryFetcher,
    private val modifiersGroupFetcher: ModifiersGroupFetcher,
    private val dishesFetcher: DishFetcher,
    private val modifiersFetcher: ModifiersFetcher,
    private val stopListService: StopListService
) : MenuService {
    private val log = Logger.withTag("MenuServiceImpl")

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val menuDataStatus = flow<DataStatus> {
        combine(
            categoryFetcher.getDataStatus(),
            modifiersGroupFetcher.getDataStatus(),
            dishesFetcher.getDataStatus(),
            modifiersFetcher.getDataStatus()
        ) { cat, modisGroup, dishes, modis ->
            if (cat is Ready && dishes is Ready && modis is Ready && modisGroup is Ready) Ready
            else if (cat is Error || dishes is Error || modis is Error || modisGroup is Error) Error(
                ComponentMenuErrorException()
            )
            else if (cat is Empty || dishes is Empty || modis is Empty || modisGroup is Empty) Empty
            else if (cat is Initial || dishes is Initial || modis is Initial || modisGroup is Initial) Initial
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
            dishesFetcher.getStateFlow(),
            categoryFetcher.getStateFlow(),
            modifiersFetcher.getStateFlow(),
            modifiersGroupFetcher.getStateFlow(),
            stopListService.getStopListStateFlow()
        ) { dishes, category, modifiers, modifiersGroup, stopList ->
            log.d {
                "Combine flows to menu data flow: \n" +
                    "- dishes: $dishes \n" +
                    "- category: $category \n" +
                    "- modifiers: $modifiers \n" +
                    "- modifiersGroup: $modifiersGroup \n" +
                    "- stopList: $stopList \n"
            }
            if (
                dishes is Finished
                && category is Finished
                && modifiers is Finished
                && modifiersGroup is Finished
                && stopList is Finished
            ) {
                try {
                    FetchState.success(
                        collectMenuData(
                            rootCategory = category.result.getOrThrow(),
                            rootModifiersGroup = modifiersGroup.result.getOrThrow(),
                            dishes = dishes.result.getOrThrow(),
                            modifiers = modifiers.result.getOrThrow(),
                            stopList = stopList.result.getOrThrow().items
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

    override fun getMenuDataStateFlow(): StateFlow<FetchState<MenuData>> =
        menuDataStateFlow

    override fun getMenuDataStatusStateFlow(): StateFlow<DataStatus> =
        menuDataStatus

    override suspend fun refreshMenu() {
        categoryFetcher.refresh()
        modifiersGroupFetcher.refresh()
        dishesFetcher.refresh()
        modifiersFetcher.refresh()
    }

    override suspend fun refreshMenuFromApi() {
        coroutineScope {
            awaitAll(
                async { categoryFetcher.refreshFromRemote() },
                async { modifiersGroupFetcher.refreshFromRemote() },
                async { dishesFetcher.refreshFromRemote() },
                async { modifiersFetcher.refreshFromRemote() }
            )
        }
    }

    private fun collectMenuData(
        rootCategory: Category,
        rootModifiersGroup: ModifiersGroup,
        dishes: List<Dish>,
        modifiers: List<DishModifier>,
        stopList: List<StopListItem>
    ): MenuData {
        log.d("Start collect menu data for: \n" +
                " - rootCategory: $rootCategory \n" +
                " - rootModifiersGroup: $rootModifiersGroup \n" +
                " - dishes: $dishes \n" +
                " - modifiers: $modifiers \n" +
                " - stopList: $stopList")
        return MenuData(
            rootCategory = rootCategory.toDetailed(null, dishes, stopList),
            rootModifiersGroup = rootModifiersGroup.toDetailed(null, modifiers),
            dishes = dishes.map { it.toDetailed(stopList) }.toList(),
            modifiers = modifiers
        )
    }
}