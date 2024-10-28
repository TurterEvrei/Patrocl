package org.turter.patrocl.data.service

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.turter.patrocl.data.mapper.toOrderWithDetails
import org.turter.patrocl.data.mapper.toPayload
import org.turter.patrocl.domain.client.OrderApiClient
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.Message
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.OrderPreview
import org.turter.patrocl.domain.model.order.OrderWithDetails
import org.turter.patrocl.domain.model.order.SavedOrderItem
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

    private val checkCurrentOrderFlow = MutableSharedFlow<String>(replay = 1)

    private val checkOrdersStateEvent = MutableSharedFlow<Unit>(replay = 1)

    private val ordersStateFlow = flow<FetchState<List<OrderPreview>>> {
        checkOrdersStateEvent.emit(Unit)
        checkOrdersStateEvent.collect {
            emit(FetchState.loading())
            try {
                orderApiClient.getActiveOrdersFlow().collect { orders ->
                    emit(FetchState.done(orders))
                    log.d { "Emit new orders from ws: $orders" }
                }
            } catch (e: Exception) {
                log.e { "Catch exception while collecting orders flow from ws. Exception: $e" }
                e.printStackTrace()
                emit(FetchState.fail(e))
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    //TODO delete
    init {
        coroutineScope.launch {
            while (true) {
                messageService.setMessage(Message.success("TEST MESSAGE FROM ORDER SERVICE"))
                delay(3000)
            }
        }
    }

    override fun getOrderFlow(guid: String): StateFlow<FetchState<OrderWithDetails>> = flow {
        log.d { "Creating new orders flow for order with guid: $guid" }
        checkCurrentOrderFlow.emit(guid)
        checkCurrentOrderFlow.collect { checkGuid ->
            emit(FetchState.loading())
            log.d {
                "Collect checkCurrentOrderFlow with checkGuid: $checkGuid \n " +
                        "in state for order: $guid"
            }
            if (checkGuid == guid) {
                val result = orderApiClient.getOrderByGuid(guid).map { it.toOrderWithDetails() }
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
    ): Result<OrderWithDetails> {
        log.d { "Start creating order" }
        val result = orderApiClient.createOrder(
            payload = toPayload(
                tableCode = table.code,
                waiterCode = waiter.code,
                orderItems = orderItems
            )
        ).map { it.toOrderWithDetails() }
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

    override suspend fun updateOrder(
        orderGuid: String,
        waiterCode: String,
        tableCode: String,
        orderItems: List<NewOrderItem>
    ): Result<OrderWithDetails> {
        log.d { "Start updating order" }
        val result = orderApiClient.updateOrder(
            guid = orderGuid,
            payload = toPayload(
                tableCode = tableCode,
                waiterCode = waiterCode,
                orderItems = orderItems
            )
        ).map { it.toOrderWithDetails() }
        result.fold(
            onSuccess = { order ->
                log.d { "Order updated: ${order.name}" }
                messageService.setMessage(Message.success("Order updated: ${order.name}"))
                checkCurrentOrderFlow.emit(orderGuid)
            },
            onFailure = { cause ->
                log.e { "Catch exception while creating order: $cause" }
                cause.printStackTrace()
                messageService.setMessage(Message.error(cause))
            }
        )
        return result
    }

    override suspend fun removeItemFromOrder(
        orderGuid: String,
        orderItem: SavedOrderItem,
        quantity: Float
    ): Result<Unit> {
        log.d { "Start removing item: ${orderItem.dishName}" }
        val result = orderApiClient.removeItem(
            orderGuid = orderGuid,
            sessionUni = orderItem.sessionUni,
            dishUni = orderItem.uni,
            dishCode = orderItem.code,
            quantity = quantity
        )
        result.fold(
            onSuccess = {
                log.d { "Success removing item: ${orderItem.dishName}" }
                checkCurrentOrderFlow.emit(orderGuid)
                messageService.setMessage(
                    Message.success("Removing item: ${orderItem.dishName} is successful")
                )
            },
            onFailure = { cause ->
                log.e { "Failure removing item: ${orderItem.dishName}. Exception: $cause" }
                cause.printStackTrace()
                messageService.setMessage(Message.error(cause))
            }
        )
        return result
    }
}