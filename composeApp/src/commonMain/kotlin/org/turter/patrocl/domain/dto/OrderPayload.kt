package org.turter.patrocl.domain.dto

import kotlinx.serialization.Serializable

@Serializable
class OrderPayload(
    val tableCode: String,
    val waiterCode: String,
    val dishList: List<OrderItemPayload>
) {

    @Serializable
    class OrderItemPayload(
        val dishId: String,
        val quantity: Float,
        val modifiers: List<ModifierPayload>
    ) {

        @Serializable
        class ModifierPayload(
            val modifierId: String,
            val quantity: Int,
            val content: String = ""
        )

    }

}