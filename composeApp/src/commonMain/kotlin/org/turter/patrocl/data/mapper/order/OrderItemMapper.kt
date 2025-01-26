package org.turter.patrocl.data.mapper.order

import org.turter.patrocl.data.dto.order.request.ModifierPayload
import org.turter.patrocl.data.dto.order.request.OrderItemPayload
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.Order

fun List<NewOrderItem>.toOrderItemListPayload(): List<OrderItemPayload.Add> =
    map { item ->
        OrderItemPayload.Add(
            dishId = item.dishId,
            quantity = item.quantity,
            modifiers = item.modifiers.toModifierListPayload()
        )
    }

fun List<NewOrderItem.Modifier>.toModifierListPayload(): List<ModifierPayload> =
    map { modifier ->
        ModifierPayload(
            type = modifier.type,
            modifierId = modifier.modifierId,
            quantity = modifier.quantity,
            content = modifier.content
        )
    }

fun List<Order.Dish>.toRemoveItemsPayload(): List<OrderItemPayload.Remove> =
    this.map { dish ->
        OrderItemPayload.Remove(
            dishId = dish.id,
            dishCode = dish.code,
            dishUni = dish.uni,
            quantity = dish.quantity
        )
    }