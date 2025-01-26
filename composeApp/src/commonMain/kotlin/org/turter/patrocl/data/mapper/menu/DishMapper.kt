package org.turter.patrocl.data.mapper.menu

import org.turter.patrocl.data.dto.source.DishDto
import org.turter.patrocl.data.local.entity.DishLocal
import org.turter.patrocl.domain.model.menu.Dish
import org.turter.patrocl.domain.model.menu.DishDetailed
import org.turter.patrocl.domain.model.stoplist.StopListItem

fun DishDto.toDishLocal(): DishLocal =
    DishLocal().let { target ->
        target.id = id
        target.guid = guid
        target.code = code
        target.name = name
        target.status = status
        target.mainParentIdent = mainParentIdent
        return@let target
    }

fun List<DishDto>.toDishListFromDto(): List<Dish> =
    map { dto ->
        Dish(
            id = dto.id,
            guid = dto.guid,
            code = dto.code,
            name = dto.name,
            status = dto.status,
            mainParentIdent = dto.mainParentIdent
        )
    }.toList()

fun List<DishDto>.toDishLocalList(): List<DishLocal> =
    map { it.toDishLocal() }.toList()

fun List<DishLocal>.toDishListFromLocal(): List<Dish> =
    map { dto ->
        Dish(
            id = dto.id,
            guid = dto.guid,
            code = dto.code,
            name = dto.name,
            status = dto.status,
            mainParentIdent = dto.mainParentIdent
        )
    }.toList()

fun Dish.toDetailed(stopList: List<StopListItem>): DishDetailed {
    val stopListItem = stopList.find { item -> item.dishId == this.id }
    return DishDetailed(
        id = this.id,
        guid = this.guid,
        code = this.code,
        name = this.name,
        status = this.status,
        mainParentIdent = this.mainParentIdent,
        onStop = stopListItem?.onStop ?: false,
        remainingCount = stopListItem?.remainingCount ?: Int.MAX_VALUE
    )
}