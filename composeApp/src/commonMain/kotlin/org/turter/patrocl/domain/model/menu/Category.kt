package org.turter.patrocl.domain.model.menu

data class Category(
    val id: String,
    val guid: String,
    val code: String,
    val name: String,
    val status: String,
    val mainParentIdent: String,
    val childList: List<Category>,
    val dishIdList: List<String>
)
