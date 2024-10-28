package org.turter.patrocl.domain.model.person

data class Employee(
    val id: String,
    val name: String,
    val lastName: String,
    val patronymic: String,
    val simpleName: String,
    val active: Boolean,
    val position: PositionEmbedded,
    val userId: String,
    val companyList: List<CompanyEmbedded>
) {
    data class CompanyEmbedded(
        val id: String,
        val title: String
    )

    data class PositionEmbedded(
        val id: String,
        val title: String,
        val specialization: String,
        val rankWeight: Int
    )
}