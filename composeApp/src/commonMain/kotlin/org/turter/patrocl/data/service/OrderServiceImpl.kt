package org.turter.patrocl.data.service

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import org.turter.patrocl.data.dto.order.response.OrdersListApiResponse
import org.turter.patrocl.data.mapper.order.toAddItemsPayload
import org.turter.patrocl.data.mapper.order.toCreateOrderPayload
import org.turter.patrocl.data.mapper.order.toOrder
import org.turter.patrocl.data.mapper.order.toOrderList
import org.turter.patrocl.data.mapper.order.toRemoveItemsFromOrderPayload
import org.turter.patrocl.data.remote.client.OrderApiClient
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.Message
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.Order
import org.turter.patrocl.domain.model.order.OrderPreview
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.domain.service.MessageService
import org.turter.patrocl.domain.service.OrderService

class OrderServiceImpl(
    private val orderApiClient: OrderApiClient,
    private val messageService: MessageService
) : OrderService {
    private val log = Logger.withTag("OrderRepositoryImpl")

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val checkCurrentOrderFlow = MutableSharedFlow<Unit>(replay = 1)

    private val checkOrdersStateEvent = MutableSharedFlow<Unit>(replay = 1)

    private val ordersStateFlow = flow<FetchState<List<OrderPreview>>> {
        checkOrdersStateEvent.emit(Unit)
        checkOrdersStateEvent.collect {
            emit(FetchState.loading())
            try {
                orderApiClient.getActiveOrdersFlow().collect { result ->
                    val data = result.getOrThrow().let {
                        if (it.status == OrdersListApiResponse.Status.ERROR) {
                            Result.failure(RuntimeException(it.message))
                        } else {
                            Result.success(it.orders.toOrderList())
                        }
                    }
                    emit(FetchState.done(data))
                    log.d { "Emit new orders flow: $data" }
                }
            } catch (e: Exception) {
                log.e { "Catch exception while collecting orders flow. Exception: $e" }
                e.printStackTrace()
                emit(FetchState.fail(e))
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    override fun getOrderFlow(guid: String): StateFlow<FetchState<Order>> = flow {
        log.d { "Creating new orders flow for order with guid: $guid" }
        checkCurrentOrderFlow.emit(Unit)
        checkCurrentOrderFlow.collect {
            emit(FetchState.loading())
            log.d {
                "Collect checkCurrentOrderFlow for order: $guid"
            }
            val result = orderApiClient.getOrderByGuid(guid).map { it.toOrder() }
            result.fold(
                onSuccess = { order ->
                    log.d { "Get order by guid from api. Order name: ${order.name}" }
                },
                onFailure = { cause ->
                    log.e { "Catch exception while getting order by guid: $cause" }
                    messageService.setMessage(Message.error(cause))
                }
            )
            log.d { "Emit result: $result" }
            emit(FetchState.done(result))
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    override fun getActiveOrdersStateFlow(): StateFlow<FetchState<List<OrderPreview>>> =
        ordersStateFlow

    override suspend fun refreshOrders() = checkOrdersStateEvent.emit(Unit)

    override suspend fun createOrder(
        table: Table,
        waiter: Waiter,
        orderItems: List<NewOrderItem>
    ): Result<Order> {
        log.d { "Start creating order" }
        val result = orderApiClient.createOrder(
            payload = toCreateOrderPayload(
                tableCode = table.code,
                waiterCode = waiter.code,
                orderItems = orderItems
            )
        ).map { it.toOrder() }
        result.fold(
            onSuccess = { order ->
                log.d { "Order created: ${order.name}" }
                messageService.setMessage(Message.success("Order created: ${order.name}"))
            },
            onFailure = { cause ->
                log.e { "Catch exception while creating order: $cause" }
                cause.printStackTrace()
                messageService.setMessage(Message.error(cause))
            }
        )
        return result
    }

    override suspend fun addItemsToOrder(
        orderGuid: String,
        newItems: List<NewOrderItem>
    ): Result<Order> {
        log.d { "Start updating order" }
        val result = orderApiClient
            .updateOrder(payload = newItems.toAddItemsPayload(orderGuid = orderGuid))
            .map { it.toOrder() }
        result.fold(
            onSuccess = { order ->
                log.d { "Order updated: ${order.name}" }
                messageService.setMessage(Message.success("Order updated: ${order.name}"))
            },
            onFailure = { cause ->
                log.e { "Catch exception while creating order: $cause" }
                cause.printStackTrace()
                messageService.setMessage(Message.error(cause))
            }
        )
        return result
    }

    override suspend fun removeItemFromOrderSession(
        orderGuid: String,
        payload: Order.Session
    ): Result<Order> {
        log.d { "Start removing items: ${payload.dishes.map { it.name }}" }
        val result = orderApiClient
            .removeItem(payload = payload.toRemoveItemsFromOrderPayload(orderGuid))
            .map { it.toOrder() }
        result.fold(
            onSuccess = {
                log.d { "Success removing items: ${payload.dishes.map { it.name }}" }
                messageService.setMessage(
                    Message.success("Removing item: ${payload.dishes.count()} is successful")
                )
            },
            onFailure = { cause ->
                log.e { "Failure removing items: ${payload.dishes.count()}. Exception: $cause" }
                cause.printStackTrace()
                messageService.setMessage(Message.error(cause))
            }
        )
        return result
    }

    override suspend fun removeItemsFromOrderSessions(
        orderGuid: String,
        payload: List<Order.Session>
    ): Result<Order> {
        log.d { "Start removing items: " +
                "${payload.flatMap { it.dishes.map { dish -> dish.name } }}" }
        val result = orderApiClient
            .removeItem(payload = payload.toRemoveItemsFromOrderPayload(orderGuid))
            .map { it.toOrder() }
        result.fold(
            onSuccess = {
                log.d { "Success removing items: " +
                        "${payload.flatMap { it.dishes.map { dish -> dish.name } }}" }
                messageService.setMessage(
                    Message.success("Removing item: " +
                            "${payload.flatMap { it.dishes }.count()} is successful")
                )
            },
            onFailure = { cause ->
                log.e { "Failure removing items: ${payload.flatMap { it.dishes }.count()}. " +
                        "Exception: $cause" }
                cause.printStackTrace()
                messageService.setMessage(Message.error(cause))
            }
        )
        return result
    }
}