package org.turter.patrocl.data_mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import org.turter.patrocl.data.mapper.menu.toDetailed
import org.turter.patrocl.data_mock.utils.MenuDataSupplier
import org.turter.patrocl.data_mock.utils.StopListDataSupplier
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.FetchState.Finished
import org.turter.patrocl.domain.model.menu.Category
import org.turter.patrocl.domain.model.menu.CategoryDetailed
import org.turter.patrocl.domain.model.menu.Dish
import org.turter.patrocl.domain.model.menu.DishDetailed
import org.turter.patrocl.domain.model.menu.DishModifier
import org.turter.patrocl.domain.model.menu.MenuData
import org.turter.patrocl.domain.model.menu.ModifiersGroup
import org.turter.patrocl.domain.model.menu.ModifiersGroupDetailed
import org.turter.patrocl.domain.model.stoplist.StopList
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.domain.service.MenuService
import kotlin.coroutines.CoroutineContext

class MenuServiceMock : MenuService {
    private val scope = CoroutineScope(Dispatchers.Default)

    private val dishesFlow = flowOf(FetchState.success(MenuDataSupplier.getDishList()))
    private val categoryFlow = flowOf(FetchState.success(MenuDataSupplier.getCategory()))
    private val modifiersFlow = flowOf(FetchState.success(MenuDataSupplier.getModifierList()))
    private val modifiersGroupFlow =
        flowOf(FetchState.success(MenuDataSupplier.getModifiersGroup()))
    private val stopListFlow = flowOf(FetchState.success(StopListDataSupplier.getStopList()))

    private val _menuDataStatusStateFlow = MutableStateFlow<DataStatus>(DataStatus.Initial)

    private val menuStateFlow = combine(
        dishesFlow, categoryFlow, modifiersFlow, modifiersGroupFlow, stopListFlow
    ) { dishes, category, modifiers, modifiersGroup, stopList ->
        if (
            dishes is Finished
            && category is Finished
            && modifiers is Finished
            && modifiersGroup is Finished
            && stopList is Finished
        ) {
            try {
                val res = FetchState.success(
                    this.collectMenuData(
                        rootCategory = category.result.getOrThrow(),
                        rootModifiersGroup = modifiersGroup.result.getOrThrow(),
                        dishes = dishes.result.getOrThrow(),
                        modifiers = modifiers.result.getOrThrow(),
                        stopList = stopList.result.getOrThrow().items
                    )
                )
                _menuDataStatusStateFlow.value = DataStatus.Ready
                res
            } catch (e: Throwable) {
                _menuDataStatusStateFlow.value = DataStatus.Error(e)
                FetchState.fail(e)
            }
        } else {
            _menuDataStatusStateFlow.value = DataStatus.Loading
            FetchState.loading()
        }
    }.stateIn(
        scope = scope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    override fun getMenuDataStateFlow(): StateFlow<FetchState<MenuData>> =
        menuStateFlow

    override fun getMenuDataStatusStateFlow(): StateFlow<DataStatus> =
        _menuDataStatusStateFlow.asStateFlow()

    override suspend fun refreshMenu() {

    }

    override suspend fun refreshMenuFromApi() {

    }

    private fun collectMenuData(
        rootCategory: Category,
        rootModifiersGroup: ModifiersGroup,
        dishes: List<Dish>,
        modifiers: List<DishModifier>,
        stopList: List<StopListItem>
    ) = MenuData(
        rootCategory = rootCategory.toDetailed(null, dishes, stopList),
        rootModifiersGroup = rootModifiersGroup.toDetailed(null, modifiers),
        dishes = dishes.map { it.toDetailed(stopList) }.toList(),
        modifiers = modifiers
    )

}