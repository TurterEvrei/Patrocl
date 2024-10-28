package org.turter.patrocl.di

import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.turter.patrocl.data.service.AuthServiceImpl
import org.turter.patrocl.data.client.HttpClientInitializer
import org.turter.patrocl.data.auth.OidcClientInitializer
import org.turter.patrocl.data.client.EmployeeApiClientImpl
import org.turter.patrocl.data.client.MenuApiClientImpl
import org.turter.patrocl.data.client.OrderApiClientImpl
import org.turter.patrocl.data.client.TableApiClientImpl
import org.turter.patrocl.data.client.WaiterApiClientImpl
import org.turter.patrocl.data.client.WebSocketFlowFactory
import org.turter.patrocl.data.local.CategoryLocalSource
import org.turter.patrocl.data.local.DishLocalSource
import org.turter.patrocl.data.local.DishModifierLocalSource
import org.turter.patrocl.data.local.EmployeeLocalSource
import org.turter.patrocl.data.local.TableLocalSource
import org.turter.patrocl.data.local.WaiterLocalRepositoryImpl
import org.turter.patrocl.data.service.EmployeeServiceImpl
import org.turter.patrocl.data.service.MenuServiceImpl
import org.turter.patrocl.data.service.MessageServiceImpl
import org.turter.patrocl.data.service.OrderServiceImpl
import org.turter.patrocl.data.service.TableServiceImpl
import org.turter.patrocl.data.service.WaiterServiceImpl
import org.turter.patrocl.domain.client.EmployeeApiClient
import org.turter.patrocl.domain.client.MenuApiClient
import org.turter.patrocl.domain.client.OrderApiClient
import org.turter.patrocl.domain.client.TableApiClient
import org.turter.patrocl.domain.client.WaiterApiClient
import org.turter.patrocl.domain.entity.CategoryLocal
import org.turter.patrocl.domain.entity.DishLocal
import org.turter.patrocl.domain.entity.DishModifierLocal
import org.turter.patrocl.domain.entity.EmployeeLocal
import org.turter.patrocl.domain.entity.TableLocal
import org.turter.patrocl.domain.repository.LocalSource
import org.turter.patrocl.domain.repository.WaiterLocalRepository
import org.turter.patrocl.domain.service.AuthService
import org.turter.patrocl.domain.service.EmployeeService
import org.turter.patrocl.domain.service.MenuService
import org.turter.patrocl.domain.service.MessageService
import org.turter.patrocl.domain.service.OrderService
import org.turter.patrocl.domain.service.TableService
import org.turter.patrocl.domain.service.WaiterService

@OptIn(ExperimentalOpenIdConnect::class)
val dataModule = module {
    single<HttpClient> {
        val initializer: HttpClientInitializer = get()
        initializer.httpClient
    }

    single<OpenIdConnectClient> {
        val initializer: OidcClientInitializer = get()
        initializer.client
    }

    single<AuthService> {
        AuthServiceImpl(
            appAuth = get(),
            httpClient = get(),
            tokenStore = get(),
            waiterLocalRepository = get(),
            employeeLocalSource = get(named("employeeLocalSource"))
        )
    }

    single<WebSocketFlowFactory> {
        WebSocketFlowFactory(httpClient = get(), authService = get())
    }

    single<MessageService> { MessageServiceImpl() }

    single<WaiterService> {
        WaiterServiceImpl(waiterApiClient = get(), waiterLocalRepository = get())
    }

    single<MenuService> {
        MenuServiceImpl(
            menuApiClient = get(),
            categoryLocalSource = get(named("categoryLocalSource")),
            dishLocalSource = get(named("dishLocalSource")),
            modifiersLocalSource = get(named("modifiersLocalSource"))
        )
    }

    single<TableService> {
        TableServiceImpl(
            tableApiClient = get(),
            tableLocalSource = get(named("tableLocalSource"))
        )
    }

    single<EmployeeService> {
        EmployeeServiceImpl(
            employeeApiClient = get(),
            employeeLocalSource = get(named("employeeLocalSource"))
        )
    }

    single<OrderService> { OrderServiceImpl(orderApiClient = get(), messageService = get()) }

    single<MenuApiClient> { MenuApiClientImpl(httpClient = get()) }

    single<TableApiClient> { TableApiClientImpl(httpClient = get()) }

    single<OrderApiClient> { OrderApiClientImpl(httpClient = get(), webSocketFlowFactory = get()) }

    single<WaiterApiClient> { WaiterApiClientImpl(httpClient = get()) }

    single<EmployeeApiClient> { EmployeeApiClientImpl(httpClient = get()) }

    single<WaiterLocalRepository> { WaiterLocalRepositoryImpl() }

    single<LocalSource<CategoryLocal>>(named("categoryLocalSource")) { CategoryLocalSource() }

    single<LocalSource<List<DishLocal>>>(named("dishLocalSource")) { DishLocalSource() }

    single<LocalSource<List<DishModifierLocal>>>(named("modifiersLocalSource")) { DishModifierLocalSource() }

    single<LocalSource<List<TableLocal>>>(named("tableLocalSource")) { TableLocalSource() }

    single<LocalSource<EmployeeLocal>>(named("employeeLocalSource")) { EmployeeLocalSource() }

}