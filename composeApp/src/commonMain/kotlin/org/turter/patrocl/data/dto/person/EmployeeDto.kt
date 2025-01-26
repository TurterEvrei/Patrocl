package org.turter.patrocl.data.dto.person

import kotlinx.serialization.Serializable
import org.turter.patrocl.domain.model.enums.Specialization

@Serializable
data class EmployeeDto(
    val id: String,
    val name: String,
    val lastName: String,
    val patronymic: String,
    val simpleName: String?,
    val active: Boolean,
    val position: PositionEmbeddedDto,
    val preferredCompanyId: String,
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
        val specialization: Specialization,
        val rankWeight: Int
    )
}