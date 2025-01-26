package org.turter.patrocl.data.remote.client.impl

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.sse.sse
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import org.turter.patrocl.data.dto.order.request.CreateOrderPayload
import org.turter.patrocl.data.dto.order.request.OrderSessionPayload
import org.turter.patrocl.data.dto.order.request.RemoveItemsFromOrderPayload
import org.turter.patrocl.data.dto.order.response.OrderDto
import org.turter.patrocl.data.dto.order.response.OrdersListApiResponse
import org.turter.patrocl.data.dto.stoplist.StopListDto
import org.turter.patrocl.data.remote.client.ApiEndpoint
import org.turter.patrocl.data.remote.client.OrderApiClient
import org.turter.patrocl.data.remote.client.proceedRequest
import org.turter.patrocl.domain.model.order.OrderPreview

class OrderApiClientImpl(
    private val httpClient: HttpClient
): OrderApiClient {
    private val log = Logger.withTag("OrderApiClientImpl")

    override suspend fun getOrderByGuid(guid: String): Result<OrderDto> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Order.getOrder(guid)) },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override suspend fun getActiveOrders(): Result<List<OrderPreview>> =
        proceedRequest(
            action = { httpClient.get(ApiEndpoint.Order.getOpenedOrdersList()) },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override suspend fun getActiveOrdersFlow(): Flow<Result<OrdersListApiResponse>> {
        return callbackFlow {
            try {
                httpClient.sse(ApiEndpoint.Order.getOpenedOrdersListFlow()) {
                    incoming.collect { event ->
                        log.d(event.toString())
                        val data = event.data
                        if (!data.isNullOrBlank()) {
                            val result: Result<OrdersListApiResponse> = try {
                                Result.success(Json.decodeFromString(data))
                            } catch (e: Exception) {
                                Result.failure(e)
                            }
                            trySend(result)
                        }
                    }
                }
            } catch (e: Exception) {
                trySend(Result.failure(e))
                close()
            }
        }
    }

    override suspend fun createOrder(payload: CreateOrderPayload): Result<OrderDto> =
        proceedRequest(
            action = { httpClient.post(ApiEndpoint.Order.createOrder()) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            } },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override suspend fun updateOrder(payload: OrderSessionPayload.AddDishes): Result<OrderDto> =
        proceedRequest(
            action = { httpClient.post(ApiEndpoint.Order.addItemsToOrder()) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            } },
            decoder = { Json.decodeFromString(it.body()) }
        )

    override suspend fun removeItem(payload: RemoveItemsFromOrderPayload): Result<OrderDto> =
        proceedRequest(
            action = { httpClient.post(ApiEndpoint.Order.removeItemsFromOrder()) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            } },
            decoder = { Json.decodeFromString(it.body()) }
        )
}