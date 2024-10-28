package org.turter.patrocl.di

import org.koin.dsl.module
import org.turter.patrocl.presentation.main.MainViewModel
import org.turter.patrocl.presentation.orders.create.CreateOrderViewModel
import org.turter.patrocl.presentation.orders.edit.EditOrderViewModel
import org.turter.patrocl.presentation.orders.list.OrdersViewModel
import org.turter.patrocl.presentation.profile.ProfileViewModel

val viewModelModule = module {
    factory {
        MainViewModel(
            authService = get(),
            waiterService = get(),
            messageService = get()
        )
    }

    factory { OrdersViewModel(orderService = get()) }

    factory {
        CreateOrderViewModel(
            menuService = get(),
            tableService = get(),
            waiterService = get(),
            orderService = get()
        )
    }

    factory { (orderGuid: String) ->
        EditOrderViewModel(
            orderGuid = orderGuid,
            menuService = get(),
            tableService = get(),
            waiterService = get(),
            orderService = get()
        )
    }

    factory {
        ProfileViewModel(
            waiterService = get(),
            employeeService = get(),
            menuService = get(),
            tableService = get()
        )
    }
}