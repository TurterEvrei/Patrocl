package org.turter.patrocl.presentation.orders.common

import org.turter.patrocl.domain.model.order.NewOrderItem

data class InterceptedAddingDish(
    val target: NewOrderItem,
    val warningType: AddingWarningType
//    val quantity: Float,
//    val modifiers: List<NewOrderItem.Modifier> = emptyList(),
//    val comments: List<String>
)

sealed class AddingWarningType {
    data object OnStop : AddingWarningType()
    data class LowRemain(val count: Int) : AddingWarningType()

    companion object {
        fun of(onStop: Boolean, remainCount: Int) = if (onStop) OnStop else LowRemain(remainCount)
    }
}