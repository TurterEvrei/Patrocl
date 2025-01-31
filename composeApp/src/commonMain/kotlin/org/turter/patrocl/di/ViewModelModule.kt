package org.turter.patrocl.di

import cafe.adriel.voyager.navigator.Navigator
import org.koin.dsl.module
import org.turter.patrocl.domain.model.menu.MenuData
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.presentation.auth.AuthViewModel
import org.turter.patrocl.presentation.main.MainViewModel
import org.turter.patrocl.presentation.orders.create.CreateOrderViewModel
import org.turter.patrocl.presentation.orders.edit.EditOrderViewModel
import org.turter.patrocl.presentation.orders.item.new.edit.EditNewOrderItemViewModel
import org.turter.patrocl.presentation.orders.list.OrdersViewModel
import org.turter.patrocl.presentation.orders.read.ReadOrderViewModel
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

    factory { OrdersViewModel(orderService = get(), waiterService = get()) }

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

    factory { (orderGuid: String, navigator: Navigator) ->
        ReadOrderViewModel(
            orderGuid = orderGuid,
            orderService = get(),
            navigator = navigator
        )
    }

    factory { (originalItem: NewOrderItem,
                  menuData: MenuData,
                  onSave: (NewOrderItem) -> Unit,
                  onDelete: (NewOrderItem) -> Unit) ->
        EditNewOrderItemViewModel(
            originalItem = originalItem,
            menuData = menuData,
            onSave = onSave,
            onDelete = onDelete
        )
    }

    factory {
        ProfileViewModel(
            waiterService = get(),
            employeeService = get(),
            menuService = get(),
            tableService = get(),
            authService = get()
        )
    }

    factory {
        StopListViewModel(stopListService = get())
    }

    factory { (currentStopList: List<StopListItem>) ->
        CreateStopListItemViewModel(
            currentStopList = currentStopList,
            dishFetcher = get(),
            stopListService = get()
        )
    }

    factory { (targetItem: StopListItem) ->
        EditStopListItemViewModel(
            targetItem = targetItem,
            stopListService = get()
        )
    }
}