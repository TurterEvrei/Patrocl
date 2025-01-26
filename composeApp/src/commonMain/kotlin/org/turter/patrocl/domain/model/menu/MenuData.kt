package org.turter.patrocl.domain.model.menu

data class MenuData(
    val rootCategory: CategoryDetailed,
    val rootModifiersGroup: ModifiersGroupDetailed,
    val dishes: List<DishDetailed>,
    val modifiers: List<DishModifier>
) {
//    val rootCategoryDetailed: CategoryDetailed
//    val rootModifiersGroupDetailed: ModifiersGroupDetailed
//
//    init {
//        rootCategoryDetailed = category.toDetailed(null)
//        rootModifiersGroupDetailed = modifiersGroup.toDetailed(null)
//    }
//
//    private fun Category.toDetailed(parent: CategoryDetailed?): CategoryDetailed {
//        val result = CategoryDetailed(
//            id = this.id,
//            guid = this.guid,
//            code = this.id,
//            name = this.id,
//            status = this.id,
//            parent = parent,
//            childList = emptyList(),
//            dishes = dishes.filter { dish -> this.dishIdList.contains(dish.id) }.toList()
//        )
//        result.childList = this.childList.map { category -> category.toDetailed(result) }.toList()
//        return result
//    }
//
//    private fun ModifiersGroup.toDetailed(parent: ModifiersGroupDetailed?): ModifiersGroupDetailed {
//        val result = ModifiersGroupDetailed(
//            id = this.id,
//            guid = this.guid,
//            code = this.id,
//            name = this.id,
//            status = this.id,
//            parent = parent,
//            childList = emptyList(),
//            modifiers = modifiers.filter { dish -> this.modifierIdList.contains(dish.id) }.toList()
//        )
//        result.childList = this.childList.map { group -> group.toDetailed(result) }.toList()
//        return result
//    }
//
//    private fun Dish.toDetailed() = DishDetailed(
//        id = this.id,
//
//    )
}
