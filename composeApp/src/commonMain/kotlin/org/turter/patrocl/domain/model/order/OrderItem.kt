package org.turter.patrocl.domain.model.order

import com.benasher44.uuid.Uuid

interface OrderItem {
    val uuid: Uuid
    var dishId: String
    var dishName: String
    var quantity: Float
    var modifiers: List<ItemModifier>
}