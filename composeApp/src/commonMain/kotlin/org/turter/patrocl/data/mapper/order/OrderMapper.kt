package org.turter.patrocl.data.mapper.order

import org.turter.patrocl.data.dto.order.request.CreateOrderPayload
import org.turter.patrocl.data.dto.order.response.OrderDto
import org.turter.patrocl.data.dto.order.response.OrderPreviewDto
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.Order
import org.turter.patrocl.domain.model.order.OrderPreview

fun OrderDto.toOrder(): Order = Order(
    guid = guid,
    name = name,
    table = Order.Table(id = table.id, code = table.code, name = table.name),
    waiter = Order.Waiter(id = waiter.id, code = waiter.code, name = waiter.name),
    openTime = openTime,
    sessions = sessions.map { it.toSession() }.toList(),
    sum = sum
)

fun OrderPreviewDto.toOrderPreview(): OrderPreview = OrderPreview(
    guid = guid,
    name = name,
    tableCode = tableCode,
    tableName = tableName,
    waiterCode = waiterCode,
    waiterName = waiterName,
    sum = sum,
    bill = bill,
    openTime = openTime
)

fun List<OrderPreviewDto>.toOrderList(): List<OrderPreview> = map(OrderPreviewDto::toOrderPreview)

fun toCreateOrderPayload(
    tableCode: String,
    waiterCode: String,
    orderItems: List<NewOrderItem>
): CreateOrderPayload {
    return CreateOrderPayload(
        tableCode = tableCode,
        waiterCode = waiterCode,
        dishList = orderItems.toOrderItemListPayload()
    )
}