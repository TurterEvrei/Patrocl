package org.turter.patrocl.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: String,
    val guid: String,
    val code: String,
    val name: String,
    val status: String,
    val mainParentIdent: String,
    val childList: List<CategoryDto>,
    val dishIdList: List<String>
)
