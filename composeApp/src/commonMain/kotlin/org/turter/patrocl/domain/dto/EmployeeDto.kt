package org.turter.patrocl.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeDto(
    val id: String,
    val name: String,
    val lastName: String,
    val patronymic: String,
    val simpleName: String?,
    val active: Boolean,
    val position: PositionEmbeddedDto,
    val userId: String,
    val companyList: List<CompanyEmbeddedDto>
) {
    @Serializable
    data class CompanyEmbeddedDto(
        val id: String,
        val title: String
    )

    @Serializable
    data class PositionEmbeddedDto(
        val id: String,
        val title: String,
        val specialization: String,
        val rankWeight: Int
    )
}