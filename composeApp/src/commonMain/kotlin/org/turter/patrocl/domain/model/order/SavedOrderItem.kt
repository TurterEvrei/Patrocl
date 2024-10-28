package org.turter.patrocl.domain.model.order

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4


data class SavedOrderItem(
    override val uuid: Uuid = uuid4(),
    override var dishId: String,
    override var dishName: String,
    override var quantity: Float,
    val code: String,
    val uni: String,
    val sessionUni: String,
    override var modifiers: List<ItemModifier>
) : OrderItem {
}
