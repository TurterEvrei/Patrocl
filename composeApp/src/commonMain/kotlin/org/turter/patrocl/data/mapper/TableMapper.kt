package org.turter.patrocl.data.mapper

import org.turter.patrocl.domain.dto.TableDto
import org.turter.patrocl.domain.entity.TableLocal
import org.turter.patrocl.domain.model.source.Table

fun TableDto.toTable(): Table = Table(
    id = id,
    guid = guid,
    code = code,
    name = name,
    status = status,
    hall = hall,
)

fun TableDto.toTableLocal(): TableLocal =
    TableLocal().let { target ->
        target.id = id
        target.guid = guid
        target.code = code
        target.name = name
        target.status = status
        target.hall = hall
        return@let target
    }

fun TableLocal.toTable() = Table(
    id = id,
    guid = guid,
    code = code,
    name = name,
    status = status,
    hall = hall,
)

fun List<TableDto>.toTableListFromDto(): List<Table> = map { it.toTable() }.toList()

fun List<TableDto>.toTableLocalList(): List<TableLocal> = map { it.toTableLocal() }.toList()

fun List<TableLocal>.toTableListFromLocal() = map { it.toTable() }.toList()