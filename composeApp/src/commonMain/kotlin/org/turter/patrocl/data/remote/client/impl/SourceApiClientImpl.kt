package org.turter.patrocl.data.remote.client.impl

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import org.turter.patrocl.data.dto.source.CategoryDto
import org.turter.patrocl.data.dto.source.DishDto
import org.turter.patrocl.data.dto.source.ModifierDto
import org.turter.patrocl.data.dto.source.ModifiersGroupDto
import org.turter.patrocl.data.remote.client.ApiEndpoint
import org.turter.patrocl.data.remote.client.SourceApiClient
import org.turter.patrocl.data.remote.client.proceedRequest

class SourceApiClientImpl(
    private val httpClient: HttpClient
): SourceApiClient {

    override suspend fun getCategoryTree(): Result<CategoryDto> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Menu.getCategoryTree()) },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override suspend fun getModifiersGroupTree(): Result<ModifiersGroupDto> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Menu.getModifiersGroupTree()) },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override suspend fun getDishes(): Result<List<DishDto>> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Menu.getDishes()) },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override suspend fun getModifiers(): Result<List<ModifierDto>> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Menu.getModifiers()) },
            decoder = { Json.decodeFromString(it.body()) }
        )

}