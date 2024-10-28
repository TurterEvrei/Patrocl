package org.turter.patrocl.data.mapper

import org.turter.patrocl.domain.dto.OrderPayload
import org.turter.patrocl.domain.dto.OrderPreviewDto
import org.turter.patrocl.domain.dto.OrderWithDetailsDto
import org.turter.patrocl.domain.model.order.ItemModifier
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.OrderPreview
import org.turter.patrocl.domain.model.order.OrderWithDetails
import org.turter.patrocl.domain.model.order.SavedOrderItem

fun OrderWithDetailsDto.toOrderWithDetails(): OrderWithDetails = OrderWithDetails(
    guid = guid,
    name = name,
    table = OrderWithDetails.Table(id = table.id, code = table.code, name = table.name),
    waiter = OrderWithDetails.Waiter(id = waiter.id, code = waiter.code, name = waiter.name),
    openTime = openTime,
    items = items
        .map { item ->
            SavedOrderItem(
                dishId = item.dishId,
                dishName = item.name,
                quantity = item.quantity,
                code = item.code,
                uni = item.uni,
                sessionUni = item.sessionUni,
                modifiers = item.modifiers
                    .map { modifier ->
                        ItemModifier(
                            modifierId = modifier.modifierId,
                            name = modifier.name,
                            quantity = modifier.quantity
                        )
                    }
            )
        }
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

fun toPayload(
    tableCode: String,
    waiterCode: String,
    orderItems: List<NewOrderItem>
): OrderPayload {
    return OrderPayload(
        tableCode = tableCode,
        waiterCode = waiterCode,
        dishList = orderItems.toOrderItemListPayload()
    )
}

private fun List<NewOrderItem>.toOrderItemListPayload(): List<OrderPayload.OrderItemPayload> =
    map { item ->
        OrderPayload.OrderItemPayload(
            dishId = item.dishId,
            quantity = item.quantity,
            modifiers = item.modifiers
                .map { modifier ->
                    OrderPayload.OrderItemPayload.ModifierPayload(
                        modifierId = modifier.modifierId,
                        quantity = modifier.quantity
                    )
                }
        )
    }
