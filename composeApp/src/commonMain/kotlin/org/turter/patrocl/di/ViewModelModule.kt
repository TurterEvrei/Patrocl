package org.turter.patrocl.di

import cafe.adriel.voyager.navigator.Navigator
import org.koin.dsl.module
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.presentation.auth.AuthViewModel
import org.turter.patrocl.presentation.main.MainViewModel
import org.turter.patrocl.presentation.orders.create.CreateOrderViewModel
import org.turter.patrocl.presentation.orders.edit.EditOrderViewModel
import org.turter.patrocl.presentation.orders.list.OrdersViewModel
import org.turter.patrocl.presentation.profile.ProfileViewModel
import org.turter.patrocl.presentation.stoplist.create.CreateStopListItemViewModel
import org.turter.patrocl.presentation.stoplist.edit.EditStopListItemViewModel
import org.turter.patrocl.presentation.stoplist.list.StopListViewModel

val viewModelModule = module {
    factory {
        AuthViewModel(
            authService = get()
        )
    }

    factory {
        MainViewModel(
            authService = get(),
            waiterService = get(),
            messageService = get()
        )
    }

    factory { OrdersViewModel(orderService = get()) }

    factory { (navigator: Navigator) ->
        CreateOrderViewModel(
            menuService = get(),
            tableService = get(),
            waiterService = get(),
            orderService = get(),
            navigator = navigator
        )
    }

    factory { (orderGuid: String, navigator: Navigator) ->
        EditOrderViewModel(
            orderGuid = orderGuid,
            menuService = get(),
            tableService = get(),
            waiterService = get(),
            orderService = get(),
            navigator = navigator
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

    factory {
        StopListViewModel(stopListService = get())
    }

    factory { (currentStopList: List<StopListItem>, navigator: Navigator) ->
        CreateStopListItemViewModel(
            currentStopList = currentStopList,
            navigator = navigator,
            dishFetcher = get(),
            stopListService = get()
        )
    }

    factory { (targetItem: StopListItem, navigator: Navigator) ->
        EditStopListItemViewModel(
            targetItem = targetItem,
            navigator = navigator,
            stopListService = get()
        )
    }
}