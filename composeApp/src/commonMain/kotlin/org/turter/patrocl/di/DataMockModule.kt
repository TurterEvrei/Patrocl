package org.turter.patrocl.di

import org.koin.dsl.module
import org.turter.patrocl.data_mock.AuthServiceMock
import org.turter.patrocl.data_mock.EmployeeServiceMock
import org.turter.patrocl.data_mock.MenuServiceMock
import org.turter.patrocl.data_mock.MessageServiceMock
import org.turter.patrocl.data_mock.OrderServiceMock
import org.turter.patrocl.data_mock.StopListServiceMock
import org.turter.patrocl.data_mock.TableServiceMock
import org.turter.patrocl.data_mock.WaiterServiceMock
import org.turter.patrocl.data_mock.fetcher.DishFetcherMock
import org.turter.patrocl.domain.fetcher.DishFetcher
import org.turter.patrocl.domain.service.AuthService
import org.turter.patrocl.domain.service.EmployeeService
import org.turter.patrocl.domain.service.MenuService
import org.turter.patrocl.domain.service.MessageService
import org.turter.patrocl.domain.service.OrderService
import org.turter.patrocl.domain.service.StopListService
import org.turter.patrocl.domain.service.TableService
import org.turter.patrocl.domain.service.WaiterService

val dataMockModule = module {

    single<AuthService> { AuthServiceMock() }

    single<EmployeeService> { EmployeeServiceMock() }

    single<MenuService> { MenuServiceMock() }

    single<MessageService> { MessageServiceMock() }

    single<OrderService> { OrderServiceMock(messageService = get()) }

    single<StopListService> { StopListServiceMock(messageService = get()) }

    single<TableService> { TableServiceMock() }

    single<WaiterService> { WaiterServiceMock() }

    single<DishFetcher> { DishFetcherMock() }

}