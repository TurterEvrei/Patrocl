package org.turter.patrocl.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class EditOwnWaiterPayload(
    val preferCompanyId: String
) {
}