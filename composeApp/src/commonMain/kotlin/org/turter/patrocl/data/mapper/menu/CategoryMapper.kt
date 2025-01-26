package org.turter.patrocl.data.mapper.menu

import io.realm.kotlin.ext.toRealmList
import org.turter.patrocl.data.dto.source.CategoryDto
import org.turter.patrocl.data.local.entity.CategoryLocal
import org.turter.patrocl.domain.model.menu.Category
import org.turter.patrocl.domain.model.menu.CategoryDetailed
import org.turter.patrocl.domain.model.menu.Dish
import org.turter.patrocl.domain.model.stoplist.StopListItem

fun CategoryDto.toCategory(): Category =
    Category(
        id = id,
        guid = guid,
        code = code,
        name = name,
        status = status,
        mainParentIdent = mainParentIdent,
        childList = childList.map { it.toCategory() }.toList(),
        dishIdList = dishIdList
    )

fun CategoryDto.toCategoryLocal(): CategoryLocal =
    CategoryLocal().let { target ->
        target.id = id
        target.guid = guid
        target.code = code
        target.name = name
        target.status = status
        target.mainParentIdent = mainParentIdent
        target.childList = childList.map { it.toCategoryLocal() }.toRealmList()
        target.dishIdList = dishIdList.toRealmList()
        return@let target
    }

fun CategoryLocal.toCategory(): Category =
    Category(
        id = id,
        guid = guid,
        code = code,
        name = name,
        status = status,
        mainParentIdent = mainParentIdent,
        childList = childList.map { it.toCategory() }.toList(),
        dishIdList = dishIdList
    )

fun Category.toDetailed(
    parent: CategoryDetailed?,
    allDishes: List<Dish>,
    stopList: List<StopListItem>
): CategoryDetailed {
    val result = CategoryDetailed(
        id = this.id,
        guid = this.guid,
        code = this.code,
        name = this.name,
        status = this.status,
        parent = parent,
        childList = emptyList(),
        dishes = allDishes
            .filter { dish -> this.dishIdList.contains(dish.id) }
            .map { it.toDetailed(stopList) }
            .toList()
    )
    result.childList = this.childList
        .map { category -> category.toDetailed(result, allDishes, stopList) }
        .toList()
    return result
}