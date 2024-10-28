package org.turter.patrocl.data.mapper

import io.realm.kotlin.ext.toRealmList
import org.turter.patrocl.domain.dto.CategoryDto
import org.turter.patrocl.domain.dto.DishDto
import org.turter.patrocl.domain.dto.ModifierDto
import org.turter.patrocl.domain.entity.CategoryLocal
import org.turter.patrocl.domain.entity.DishLocal
import org.turter.patrocl.domain.entity.DishModifierLocal
import org.turter.patrocl.domain.model.source.Category
import org.turter.patrocl.domain.model.source.Dish
import org.turter.patrocl.domain.model.source.DishModifier

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

fun ModifierDto.toModifierLocal(): DishModifierLocal =
    DishModifierLocal().let { target ->
        target.id = id
        target.guid = guid
        target.code = code
        target.name = name
        target.status = status
        target.mainParentIdent = mainParentIdent
        return@let target
    }

fun List<ModifierDto>.toModifierListFromDto(): List<DishModifier> =
    map { dto ->
        DishModifier(
            id = dto.id,
            guid = dto.guid,
            code = dto.code,
            name = dto.name,
            status = dto.status,
            mainParentIdent = dto.mainParentIdent
        )
    }.toList()

fun List<ModifierDto>.toModifierLocalList(): List<DishModifierLocal> =
    map { it.toModifierLocal() }.toList()

fun List<DishModifierLocal>.toModifierListFromLocal(): List<DishModifier> =
    map { dto ->
        DishModifier(
            id = dto.id,
            guid = dto.guid,
            code = dto.code,
            name = dto.name,
            status = dto.status,
            mainParentIdent = dto.mainParentIdent
        )
    }.toList()