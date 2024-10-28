package org.turter.patrocl.domain.model.source

data class Dish(
    val id: String,
    val guid: String,
    val code: String,
    val name: String,
    val status: String,
    val mainParentIdent: String
) {
}