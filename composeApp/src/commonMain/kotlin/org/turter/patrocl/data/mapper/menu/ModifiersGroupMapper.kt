package org.turter.patrocl.data.mapper.menu

import io.realm.kotlin.ext.toRealmList
import org.turter.patrocl.data.dto.source.ModifiersGroupDto
import org.turter.patrocl.data.local.entity.ModifiersGroupLocal
import org.turter.patrocl.domain.model.menu.DishModifier
import org.turter.patrocl.domain.model.menu.ModifiersGroup
import org.turter.patrocl.domain.model.menu.ModifiersGroupDetailed

fun ModifiersGroupDto.toModifiersGroup(): ModifiersGroup =
    ModifiersGroup(
        id = id,
        guid = guid,
        code = code,
        name = name,
        status = status,
        mainParentIdent = mainParentIdent,
        childList = childList.map { it.toModifiersGroup() }.toList(),
        modifierIdList = modifierIdList
    )

fun ModifiersGroupDto.toModifiersGroupLocal(): ModifiersGroupLocal =
    ModifiersGroupLocal().let { target ->
        target.id = id
        target.guid = guid
        target.code = code
        target.name = name
        target.status = status
        target.mainParentIdent = mainParentIdent
        target.childList = childList.map { it.toModifiersGroupLocal() }.toRealmList()
        target.modifierIdList = modifierIdList.toRealmList()
        return@let target
    }

fun ModifiersGroupLocal.toModifiersGroup(): ModifiersGroup =
    ModifiersGroup(
        id = id,
        guid = guid,
        code = code,
        name = name,
        status = status,
        mainParentIdent = mainParentIdent,
        childList = childList.map { it.toModifiersGroup() }.toList(),
        modifierIdList = modifierIdList
    )

fun ModifiersGroup.toDetailed(
    parent: ModifiersGroupDetailed?,
    allModifiers: List<DishModifier>
): ModifiersGroupDetailed {
    val result = ModifiersGroupDetailed(
        id = this.id,
        guid = this.guid,
        code = this.id,
        name = this.id,
        status = this.id,
        parent = parent,
        childList = emptyList(),
        modifiers = allModifiers
            .filter { modifier -> this.modifierIdList.contains(modifier.id) }
            .toList()
    )
    result.childList = this.childList
        .map { group -> group.toDetailed(result, allModifiers) }
        .toList()
    return result
}