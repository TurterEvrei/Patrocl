package org.turter.patrocl.domain.service

import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.source.Category
import org.turter.patrocl.domain.model.source.Dish
import org.turter.patrocl.domain.model.source.DishModifier
import org.turter.patrocl.domain.model.source.MenuData

interface MenuService {
    fun getCategoryTreeStateFlow(): StateFlow<FetchState<Category>>
    fun getDishesStateFlow(): StateFlow<FetchState<List<Dish>>>
    fun getModifiersStateFlow(): StateFlow<FetchState<List<DishModifier>>>
    fun getMenuDataStateFlow(): StateFlow<FetchState<MenuData>>
    fun getCategoryTreeDataStatusStateFlow(): StateFlow<DataStatus>
    fun getDishesDataStatusStateFlow(): StateFlow<DataStatus>
    fun getModifiersDataStatusStateFlow(): StateFlow<DataStatus>
    fun getMenuDataStatusStateFlow(): StateFlow<DataStatus>
    suspend fun refreshMenu()
    suspend fun refreshMenuFromApi()
}