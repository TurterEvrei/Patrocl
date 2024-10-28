package org.turter.patrocl.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class WaiterDto(
    val employeeId: String,
    val rkId: String,
    val code: String,
    val name: String,
    val preferCompanyId: String
) {
}