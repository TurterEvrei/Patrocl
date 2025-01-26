package org.turter.patrocl.data.mapper

import org.turter.patrocl.data.dto.person.WaiterDto
import org.turter.patrocl.data.local.entity.WaiterLocal
import org.turter.patrocl.domain.model.person.Waiter

fun WaiterDto.toWaiterFromDto() = Waiter(
    employeeId = employeeId,
    rkId = rkId,
    code = code,
    name = name
)

fun WaiterDto.toWaiterLocalFromDto() =
    WaiterLocal().let { target ->
        target.employeeId = employeeId
        target.rkId = rkId
        target.code = code
        target.name = name
        return@let target
    }

fun WaiterLocal.toWaiterFromLocal() = Waiter(
    employeeId = employeeId,
    rkId = rkId,
    code = code,
    name = name
)