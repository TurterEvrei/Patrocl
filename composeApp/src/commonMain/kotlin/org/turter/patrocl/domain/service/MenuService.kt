package org.turter.patrocl.domain.service

import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.menu.Category
import org.turter.patrocl.domain.model.menu.Dish
import org.turter.patrocl.domain.model.menu.DishModifier
import org.turter.patrocl.domain.model.menu.MenuData

interface MenuService {
    fun getMenuDataStateFlow(): StateFlow<FetchState<MenuData>>
    fun getMenuDataStatusStateFlow(): StateFlow<DataStatus>
    suspend fun refreshMenu()
    suspend fun refreshMenuFromApi()
}