package org.turter.patrocl.data_mock.utils

import kotlinx.datetime.LocalDateTime
import org.turter.patrocl.domain.model.order.Order
import org.turter.patrocl.domain.model.order.OrderPreview
import org.turter.patrocl.utils.now

object OrderDataSupplier {

    fun getOrder(): Order {
        val waiter = Order.Waiter(id = "waiter-id-1", code = "99", name = "Бобби")
        return Order(
            guid = "order-guid-1",
            name = "33.1",
            sum = 1234.5f,
            table = Order.Table(id = "table-id-1", name = "33", code = "table-code-1"),
            waiter = waiter,
            openTime = LocalDateTime.now(),
            sessions = listOf(
                Order.Session(
                    uni = "session-uni-1",
                    lineGuid = "session-lineGuid-1",
                    sessionId = "session-sessionId-1",
                    isDraft = false,
                    remindTime = LocalDateTime.now(),
                    startService = LocalDateTime.now(),
                    printed = true,
                    cookMins = null,
                    creator = waiter,
                    dishes = listOf(
                        Order.Dish(
                            id = "dish-id-1",
                            name = "Цезарь с курицей",
                            quantity = 1f,
                            code = "dish-code-1",
                            uni = "dish-uni-1-1",
                            modifiers = listOf()
                        ),
                        Order.Dish(
                            id = "dish-id-3",
                            name = "Буритто",
                            quantity = 2f,
                            code = "dish-code-1",
                            uni = "dish-uni-1-2",
                            modifiers = listOf(
                                Order.Dish.Modifier(
                                    id = "modifier-id-1",
                                    name = "В ОДНУ ТАРЕЛКУ",
                                    quantity = 1
                                )
                            )
                        )
                    )
                ),
                Order.Session(
                    uni = "session-uni-2",
                    lineGuid = "session-lineGuid-2",
                    sessionId = "session-sessionId-2",
                    isDraft = false,
                    remindTime = LocalDateTime.now(),
                    startService = LocalDateTime.now(),
                    printed = true,
                    cookMins = null,
                    creator = Order.Waiter(id = "waiter-id-2", code = "133", name = "Билли"),
                    dishes = listOf(
                        Order.Dish(
                            id = "dish-id-3",
                            name = "Буритто",
                            quantity = 2f,
                            code = "dish-code-3",
                            uni = "dish-uni-2-1",
                            modifiers = listOf()
                        ),
                        Order.Dish(
                            id = "dish-id-4",
                            name = "Куриная отбивная",
                            quantity = 1f,
                            code = "dish-code-4",
                            uni = "dish-uni-2-2",
                            modifiers = listOf(
                                Order.Dish.Modifier(
                                    id = "modifier-id-2",
                                    name = "ЗАМЕНА",
                                    quantity = 1
                                ),
                            )
                        ),
                        Order.Dish(
                            id = "dish-id-5",
                            name = "Греча",
                            quantity = 1f,
                            code = "dish-code-5",
                            uni = "dish-uni-2-3",
                            modifiers = listOf(
                                Order.Dish.Modifier(
                                    id = "modifier-id-1",
                                    name = "В ОДНУ ТАРЕЛКУ",
                                    quantity = 1
                                ),
                                Order.Dish.Modifier(
                                    id = "modifier-id-5",
                                    name = "Не зажаривать",
                                    quantity = 1
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    fun getOrderPreviews() = listOf(
        OrderPreview(
            guid = "order-guid-1",
            name = "33.1",
            tableCode = "table-code-1",
            tableName = "33",
            waiterCode = "99",
            waiterName = "Бобби",
            sum = 1234.5f,
            bill = false,
            openTime = LocalDateTime.now()
        ),
        OrderPreview(
            guid = "order-guid-2",
            name = "34",
            tableCode = "table-code-2",
            tableName = "34",
            waiterCode = "133",
            waiterName = "Билли",
            sum = 1336.5f,
            bill = false,
            openTime = LocalDateTime.now()
        ),
        OrderPreview(
            guid = "order-guid-3",
            name = "34.1",
            tableCode = "table-code-2",
            tableName = "34",
            waiterCode = "133",
            waiterName = "Билли",
            sum = 25899.5f,
            bill = true,
            openTime = LocalDateTime.now()
        ),
        OrderPreview(
            guid = "order-guid-4",
            name = "54",
            tableCode = "table-code-3",
            tableName = "54",
            waiterCode = "99",
            waiterName = "Бобби",
            sum = 4562f,
            bill = false,
            openTime = LocalDateTime.now()
        ),
        OrderPreview(
            guid = "order-guid-5",
            name = "21",
            tableCode = "table-code-4",
            tableName = "21",
            waiterCode = "99",
            waiterName = "Бобби",
            sum = 126782.5f,
            bill = true,
            openTime = LocalDateTime.now()
        )
    )

}