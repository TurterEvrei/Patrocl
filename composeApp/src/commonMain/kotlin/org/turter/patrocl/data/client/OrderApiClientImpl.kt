package org.turter.patrocl.data.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import org.turter.patrocl.data.mapper.toOrderList
import org.turter.patrocl.domain.client.OrderApiClient
import org.turter.patrocl.domain.dto.OrderPayload
import org.turter.patrocl.domain.dto.OrderWithDetailsDto
import org.turter.patrocl.domain.dto.response.OrdersListApiResponse
import org.turter.patrocl.domain.model.order.OrderPreview

class OrderApiClientImpl(
    private val httpClient: HttpClient,
    private val webSocketFlowFactory: WebSocketFlowFactory
): OrderApiClient {
    override suspend fun getOrderByGuid(guid: String): Result<OrderWithDetailsDto> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Order.getOrder(guid)) },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override suspend fun getActiveOrders(): Result<List<OrderPreview>> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Order.getOrderList()) },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override fun getActiveOrdersFlow(): Flow<Result<List<OrderPreview>>> =
        webSocketFlowFactory.create(
            path = ApiEndpoint.Order.getWebSocketOrderList(),
            decoder = { Json.decodeFromString<OrdersListApiResponse>(it).orders.toOrderList() }
        )

    override suspend fun createOrder(payload: OrderPayload): Result<OrderWithDetailsDto> =
        proceedRequest(
            action = { httpClient.post(ApiEndpoint.Order.createOrder()) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            } },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override suspend fun updateOrder(
        guid: String,
        payload: OrderPayload
    ): Result<OrderWithDetailsDto> =
        proceedRequest(
            action = { httpClient.patch(ApiEndpoint.Order.updateOrder(guid)) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            } },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override suspend fun removeItem(
        orderGuid: String,
        sessionUni: String,
        dishUni: String,
        dishCode: String,
        quantity: Float
    ): Result<Unit> =
        proceedRequest(
            action = { httpClient.delete(ApiEndpoint.Order.removeItem(orderGuid)) {
                url {
                    parameters.append("sessionUni", sessionUni)
                    parameters.append("dishUni", dishUni)
                    parameters.append("dishCode", dishCode)
                    parameters.append("quantity", quantity.toString())
                }
            } },
            decoder = { }
        )
}