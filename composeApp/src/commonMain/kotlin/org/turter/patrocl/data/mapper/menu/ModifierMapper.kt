package org.turter.patrocl.data.mapper.menu

import org.turter.patrocl.data.dto.source.ModifierDto
import org.turter.patrocl.data.local.entity.DishModifierLocal
import org.turter.patrocl.domain.model.menu.DishModifier

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