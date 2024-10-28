package org.turter.patrocl.data.mapper

import io.realm.kotlin.ext.toRealmList
import org.turter.patrocl.domain.dto.EmployeeDto
import org.turter.patrocl.domain.dto.EmployeeDto.CompanyEmbeddedDto
import org.turter.patrocl.domain.dto.EmployeeDto.PositionEmbeddedDto
import org.turter.patrocl.domain.entity.CompanyEmbeddedLocal
import org.turter.patrocl.domain.entity.EmployeeLocal
import org.turter.patrocl.domain.entity.PositionEmbeddedLocal
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
        specialization = position?.specialization ?: "",
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
        target.specialization = position.specialization
        target.rankWeight = position.rankWeight
        target
    }

private fun toCompanyEmbeddedLocal(company: CompanyEmbeddedDto): CompanyEmbeddedLocal =
    CompanyEmbeddedLocal().let { target ->
        target.id = company.id
        target.title = company.title
        target
    }