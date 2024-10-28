package org.turter.patrocl.data.mapper

import org.turter.patrocl.domain.dto.WaiterDto
import org.turter.patrocl.domain.entity.WaiterLocal
import org.turter.patrocl.domain.model.person.Waiter

fun WaiterDto.toWaiterFromDto() = Waiter(
    employeeId = employeeId,
    rkId = rkId,
    code = code,
    name = name,
    preferCompanyId = preferCompanyId
)

fun WaiterDto.toWaiterLocalFromDto() =
    WaiterLocal().let { target ->
        target.employeeId = employeeId
        target.rkId = rkId
        target.code = code
        target.name = name
        target.preferCompanyId = preferCompanyId
        return@let target
    }

fun WaiterLocal.toWaiterFromLocal() = Waiter(
    employeeId = employeeId,
    rkId = rkId,
    code = code,
    name = name,
    preferCompanyId = preferCompanyId
)