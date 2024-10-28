package org.turter.patrocl.domain.model.source

data class MenuData(
    val category: Category,
    val dishes: List<Dish>,
    val modifiers: List<DishModifier>,
)
