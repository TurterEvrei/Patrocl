package org.turter.patrocl.data.client

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import org.turter.patrocl.domain.client.MenuApiClient
import org.turter.patrocl.domain.dto.CategoryDto
import org.turter.patrocl.domain.dto.DishDto
import org.turter.patrocl.domain.dto.ModifierDto

class MenuApiClientImpl(
    private val httpClient: HttpClient
): MenuApiClient {

    override suspend fun getCategoryTree(): Result<CategoryDto> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Menu.getCategoryTree()) },
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