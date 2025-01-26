package org.turter.patrocl.data.remote.client

import org.turter.patrocl.data.dto.source.CategoryDto
import org.turter.patrocl.data.dto.source.DishDto
import org.turter.patrocl.data.dto.source.ModifierDto
import org.turter.patrocl.data.dto.source.ModifiersGroupDto

interface SourceApiClient {
    suspend fun getCategoryTree(): Result<CategoryDto>
    suspend fun getModifiersGroupTree(): Result<ModifiersGroupDto>
    suspend fun getDishes(): Result<List<DishDto>>
    suspend fun getModifiers(): Result<List<ModifierDto>>
}