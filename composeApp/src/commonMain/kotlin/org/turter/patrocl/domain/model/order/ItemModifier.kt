package org.turter.patrocl.domain.model.order

data class ItemModifier(
    val modifierId: String,
    val name: String,
    var quantity: Int
) {
}
