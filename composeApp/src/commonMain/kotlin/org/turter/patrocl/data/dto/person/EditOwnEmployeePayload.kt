package org.turter.patrocl.data.dto.person

import kotlinx.serialization.Serializable

@Serializable
data class EditOwnEmployeePayload(
    val preferredCompanyId: String
) {
}