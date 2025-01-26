package org.turter.patrocl.domain.model.menu

data class DishDetailed(
    val id: String,
    val guid: String,
    val code: String,
    val name: String,
    val status: String,
    val mainParentIdent: String,
    val onStop: Boolean = false,
    val remainingCount: Int = Int.MAX_VALUE
) {
}