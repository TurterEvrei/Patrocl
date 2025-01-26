package org.turter.patrocl.data.mapper.order

import org.turter.patrocl.data.dto.order.request.OrderItemPayload
import org.turter.patrocl.data.dto.order.request.OrderSessionPayload
import org.turter.patrocl.data.dto.order.request.RemoveItemsFromOrderPayload
import org.turter.patrocl.data.dto.order.response.OrderDto
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.Order

fun OrderDto.Session.toSession() =
    Order.Session(
        uni = uni,
        lineGuid = lineGuid,
        sessionId = sessionId,
        isDraft = isDraft,
        remindTime = remindTime,
        startService = startService,
        printed = printed,
        cookMins = cookMins,
        creator = Order.Waiter(id = creator.id, code = creator.code, name = creator.name),
        dishes = dishes.map { it.toDish() }.toList()
    )

fun OrderDto.Dish.toDish() = Order.Dish(
    id = id,
    name = name,
    quantity = quantity,
    code = code,
    uni = uni,
    modifiers = modifiers.map { it.toModifier() }.toList()
)

fun OrderDto.Dish.Modifier.toModifier() = Order.Dish.Modifier(
    id = id,
    name = name,
    quantity = quantity
)

fun List<NewOrderItem>.toAddItemsPayload(orderGuid: String): OrderSessionPayload.AddDishes =
    OrderSessionPayload.AddDishes(
        orderGuid = orderGuid,
        items = this.toOrderItemListPayload()
    )

fun Order.Session.toRemoveItemsFromOrderPayload(orderGuid: String): RemoveItemsFromOrderPayload =
    RemoveItemsFromOrderPayload(
        orderGuid = orderGuid,
        sessions = listOf(
            OrderSessionPayload.RemoveDishes(
                sessionUni = this.uni,
                items = this.dishes.toRemoveItemsPayload()
            )
        )
    )

fun List<Order.Session>.toRemoveItemsFromOrderPayload(orderGuid: String): RemoveItemsFromOrderPayload =
    RemoveItemsFromOrderPayload(
        orderGuid = orderGuid,
        sessions = this.map { session ->
            OrderSessionPayload.RemoveDishes(
                sessionUni = session.uni,
                items = session.dishes.toRemoveItemsPayload()
            )
        }
    )