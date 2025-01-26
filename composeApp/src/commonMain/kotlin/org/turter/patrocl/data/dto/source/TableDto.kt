package org.turter.patrocl.data.dto.source

import kotlinx.serialization.Serializable

@Serializable
data class TableDto(
    val id: String,
    val guid: String,
    val code: String,
    val name: String,
    val status: String,
    val hall: String
)
