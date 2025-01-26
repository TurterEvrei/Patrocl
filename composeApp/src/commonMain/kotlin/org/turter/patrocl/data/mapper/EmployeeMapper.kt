package org.turter.patrocl.data.mapper

import io.realm.kotlin.ext.toRealmList
import org.turter.patrocl.data.dto.person.EmployeeDto
import org.turter.patrocl.data.dto.person.EmployeeDto.CompanyEmbeddedDto
import org.turter.patrocl.data.dto.person.EmployeeDto.PositionEmbeddedDto
import org.turter.patrocl.data.local.entity.CompanyEmbeddedLocal
import org.turter.patrocl.data.local.entity.EmployeeLocal
import org.turter.patrocl.data.local.entity.PositionEmbeddedLocal
import org.turter.patrocl.domain.model.enums.Specialization
import org.turter.patrocl.domain.model.person.Employee
import org.turter.patrocl.domain.model.person.Employee.CompanyEmbedded

fun EmployeeDto.toEmployeeFromDto(): Employee = Employee(
    id = id,
    name = name,
    lastName = lastName,
    patronymic = patronymic,
    simpleName = simpleName ?: "",
    active = active,
    position = toPositionEmbedded(position),
    userId = userId,
    preferredCompanyId = preferredCompanyId,
    companyList = companyList.map { toCompanyEmbedded(it) }
)

fun EmployeeDto.toEmployeeLocalFromDto(): EmployeeLocal =
    EmployeeLocal().let { target ->
        target.id = id
        target.name = name
        target.lastName = lastName
        target.patronymic = patronymic
        target.simpleName = simpleName ?: ""
        target.active = active
        target.position = toPositionEmbeddedLocal(position)
        target.userId = userId
        target.preferredCompanyId = preferredCompanyId
        target.companyList = companyList.map { toCompanyEmbeddedLocal(it) }.toRealmList()
        target
    }

fun EmployeeLocal.toEmployeeFromLocal(): Employee = Employee(
    id = id,
    name = name,
    lastName = lastName,
    patronymic = patronymic,
    simpleName = simpleName ?: "",
    active = active,
    position = toPositionEmbedded(position),
    userId = userId,
    preferredCompanyId = preferredCompanyId,
    companyList = companyList.map { toCompanyEmbedded(it) }
)

private fun toPositionEmbedded(position: PositionEmbeddedDto) =
    Employee.PositionEmbedded(
        id = position.id,
        title = position.title,
        specialization = position.specialization,
        rankWeight = position.rankWeight
    )

private fun toPositionEmbedded(position: PositionEmbeddedLocal?) =
    Employee.PositionEmbedded(
        id = position?.id ?: "",
        title = position?.title ?: "",
        specialization = Specialization.valueOf(position?.specialization ?: ""),
//        specialization = position?.specialization ?: "",
        rankWeight = position?.rankWeight ?: 0
    )

private fun toCompanyEmbedded(company: CompanyEmbeddedLocal): CompanyEmbedded =
    CompanyEmbedded(
        id = company.id,
        title = company.title
    )

private fun toCompanyEmbedded(company: CompanyEmbeddedDto): CompanyEmbedded =
    CompanyEmbedded(
        id = company.id,
        title = company.title
    )

private fun toPositionEmbeddedLocal(position: PositionEmbeddedDto): PositionEmbeddedLocal =
    PositionEmbeddedLocal().let { target ->
        target.id = position.id
        target.title = position.title
        target.specialization = position.specialization.name
        target.rankWeight = position.rankWeight
        target
    }

private fun toCompanyEmbeddedLocal(company: CompanyEmbeddedDto): CompanyEmbeddedLocal =
    CompanyEmbeddedLocal().let { target ->
        target.id = company.id
        target.title = company.title
        target
    }