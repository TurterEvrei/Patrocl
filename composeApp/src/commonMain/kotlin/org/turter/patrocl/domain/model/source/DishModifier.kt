package org.turter.patrocl.domain.model.source

data class DishModifier(
    val id: String,
    val guid: String,
    val code: String,
    val name: String,
    val status: String,
    val mainParentIdent: String
)
