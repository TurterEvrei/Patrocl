package org.turter.patrocl.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class ModifierDto(
    val id: String,
    val guid: String,
    val code: String,
    val name: String,
    val status: String,
    val mainParentIdent: String
)
