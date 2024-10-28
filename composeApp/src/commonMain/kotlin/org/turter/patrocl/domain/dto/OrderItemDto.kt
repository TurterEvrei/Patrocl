package org.turter.patrocl.domain.dto

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.serialization.Serializable

@Serializable
data class OrderItemDto(
    var dishId: String,
    var name: String,
    var quantity: Float,
    val code: String,
    val uni: String,
    val sessionUni: String,
    var modifiers: List<ItemModifierDto>
) {

    @Serializable
    data class ItemModifierDto(
        val modifierId: String,
        val name: String,
        var quantity: Int
    ) {
    }
}
