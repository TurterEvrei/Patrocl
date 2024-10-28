package org.turter.patrocl.domain.model.order

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4

data class NewOrderItem(
    override val uuid: Uuid = uuid4(),
    override var dishId: String,
    override var dishName: String,
    override var quantity: Float,
    override var modifiers: List<ItemModifier>,
) : OrderItem {
}
