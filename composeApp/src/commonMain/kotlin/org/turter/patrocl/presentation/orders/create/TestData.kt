package org.turter.patrocl.presentation.orders.create

import org.turter.patrocl.domain.model.order.NewOrderItem

val initTestNewOrderItems = listOf(
    NewOrderItem(
        dishId = "dish-id-1",
        dishName = "Цезарь с курицей",
        quantity = 1f,
        modifiers = listOf(
            NewOrderItem.Modifier.regular(
                modifierId = "modifier-id-1",
                name = "В ОДНУ ТАРЕЛКУ",
                quantity = 1
            ),
            NewOrderItem.Modifier.regular(
                modifierId = "modifier-id-2",
                name = "ЗАМЕНА",
                quantity = 2
            ),
            NewOrderItem.Modifier.comment(content = "Коммент 1"),
            NewOrderItem.Modifier.comment(content = "Коммент 2")
        )
    ),
    NewOrderItem(
        dishId = "dish-id-2",
        dishName = "Салат с куриной печенью",
        quantity = 2f,
        modifiers = emptyList()
    ),
)