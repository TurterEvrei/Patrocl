package org.turter.patrocl.data.dto.order.request

import kotlinx.serialization.Serializable
import org.turter.patrocl.domain.model.order.NewOrderItem.Modifier.Type

@Serializable
data class ModifierPayload(
    val type: Type,
    val modifierId: String,
    val quantity: Int,
    val content: String
)

