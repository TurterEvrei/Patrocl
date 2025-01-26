package org.turter.patrocl.data.dto.source

import kotlinx.serialization.Serializable

@Serializable
data class ModifiersGroupDto(
    val id: String,
    val guid: String,
    val code: String,
    val name: String,
    val status: String,
    val mainParentIdent: String,
    val childList: List<ModifiersGroupDto>,
    val modifierIdList: List<String>
)
