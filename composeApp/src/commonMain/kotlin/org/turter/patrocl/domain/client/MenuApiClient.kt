package org.turter.patrocl.domain.client

import org.turter.patrocl.domain.dto.CategoryDto
import org.turter.patrocl.domain.dto.DishDto
import org.turter.patrocl.domain.dto.ModifierDto

interface MenuApiClient {
    suspend fun getCategoryTree(): Result<CategoryDto>
    suspend fun getDishes(): Result<List<DishDto>>
    suspend fun getModifiers(): Result<List<ModifierDto>>
}