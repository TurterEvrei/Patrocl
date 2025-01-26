package org.turter.patrocl.data_mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.delayFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import org.turter.patrocl.data_mock.utils.OrderDataSupplier
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.Message
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.Order
import org.turter.patrocl.domain.model.order.OrderPreview
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.domain.service.MessageService
import org.turter.patrocl.domain.service.OrderService
import kotlin.random.Random

class OrderServiceMock(
    private val messageService: MessageService
): OrderService {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val orderFlow = MutableStateFlow(FetchState.success(OrderDataSupplier.getOrder()))

    private val randomIntFlow = flow {
        while (true) {
            emit(Random.nextInt(1, 5))
            delay(3000)
        }
    }

    private val ordersFlow = flow {
        emit(FetchState.loading())
        delay(500)
        randomIntFlow.collect { i ->
            emit(
                FetchState.success(
                    OrderDataSupplier.getOrderPreviews()
                        .filterIndexed { index, _ -> i != index }
                        .toList()
                )
            )
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    override fun getOrderFlow(guid: String): StateFlow<FetchState<Order>> =
        orderFlow.asStateFlow()

    override fun getActiveOrdersStateFlow(): StateFlow<FetchState<List<OrderPreview>>> =
        ordersFlow

    override suspend fun refreshOrders() {

    }

    override suspend fun createOrder(
        table: Table,
        waiter: Waiter,
        orderItems: List<NewOrderItem>
    ): Result<Order> {
        delay(2000)
        messageService.setMessage(Message.success("Order created"))
        return Result.success(OrderDataSupplier.getOrder())
    }

    override suspend fun addItemsToOrder(
        orderGuid: String,
        newItems: List<NewOrderItem>
    ): Result<Order> {
        delay(2000)
        messageService.setMessage(Message.success("Items saved"))
        return Result.success(OrderDataSupplier.getOrder())
    }

    override suspend fun removeItemFromOrderSession(
        orderGuid: String,
        payload: Order.Session
    ): Result<Order> {
        delay(2000)
        messageService.setMessage(Message.success("Item removed"))
        return Result.success(OrderDataSupplier.getOrder())
    }

    override suspend fun removeItemsFromOrderSessions(
        orderGuid: String,
        payload: List<Order.Session>
    ): Result<Order> {
        delay(2000)
        messageService.setMessage(Message.success("Items removed"))
        return Result.success(OrderDataSupplier.getOrder())
    }
}