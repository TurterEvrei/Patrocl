package org.turter.patrocl.di

import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.turter.patrocl.data.auth.OidcClientInitializer
import org.turter.patrocl.data.fetcher.CategoryFetcherImpl
import org.turter.patrocl.data.fetcher.DishFetcherImpl
import org.turter.patrocl.data.fetcher.ModifiersFetcherImpl
import org.turter.patrocl.data.fetcher.ModifiersGroupFetcherImpl
import org.turter.patrocl.data.fetcher.TableFetcherImpl
import org.turter.patrocl.data.local.LocalSource
import org.turter.patrocl.data.local.WaiterLocalRepository
import org.turter.patrocl.data.local.entity.CategoryLocal
import org.turter.patrocl.data.local.entity.DishLocal
import org.turter.patrocl.data.local.entity.DishModifierLocal
import org.turter.patrocl.data.local.entity.EmployeeLocal
import org.turter.patrocl.data.local.entity.ModifiersGroupLocal
import org.turter.patrocl.data.local.entity.TableLocal
import org.turter.patrocl.data.local.impl.CategoryLocalSource
import org.turter.patrocl.data.local.impl.DishLocalSource
import org.turter.patrocl.data.local.impl.DishModifierLocalSource
import org.turter.patrocl.data.local.impl.EmployeeLocalSource
import org.turter.patrocl.data.local.impl.ModifiersGroupLocalSource
import org.turter.patrocl.data.local.impl.TableLocalSource
import org.turter.patrocl.data.local.impl.WaiterLocalRepositoryImpl
import org.turter.patrocl.data.remote.client.EmployeeApiClient
import org.turter.patrocl.data.remote.client.OrderApiClient
import org.turter.patrocl.data.remote.client.SourceApiClient
import org.turter.patrocl.data.remote.client.StopListApiClient
import org.turter.patrocl.data.remote.client.TableApiClient
import org.turter.patrocl.data.remote.client.WaiterApiClient
import org.turter.patrocl.data.remote.client.impl.EmployeeApiClientImpl
import org.turter.patrocl.data.remote.client.impl.OrderApiClientImpl
import org.turter.patrocl.data.remote.client.impl.SourceApiClientImpl
import org.turter.patrocl.data.remote.client.impl.StopListApiClientImpl
import org.turter.patrocl.data.remote.client.impl.TableApiClientImpl
import org.turter.patrocl.data.remote.client.impl.WaiterApiClientImpl
import org.turter.patrocl.data.remote.config.HttpClientInitializer
import org.turter.patrocl.data.service.AuthServiceImpl
import org.turter.patrocl.data.service.EmployeeServiceImpl
import org.turter.patrocl.data.service.MenuServiceImpl
import org.turter.patrocl.data.service.MessageServiceImpl
import org.turter.patrocl.data.service.OrderServiceImpl
import org.turter.patrocl.data.service.StopListServiceImpl
import org.turter.patrocl.data.service.TableServiceImpl
import org.turter.patrocl.data.service.WaiterServiceImpl
import org.turter.patrocl.domain.fetcher.CategoryFetcher
import org.turter.patrocl.domain.fetcher.DishFetcher
import org.turter.patrocl.domain.fetcher.ModifiersFetcher
import org.turter.patrocl.domain.fetcher.ModifiersGroupFetcher
import org.turter.patrocl.domain.fetcher.TableFetcher
import org.turter.patrocl.domain.service.AuthService
import org.turter.patrocl.domain.service.EmployeeService
import org.turter.patrocl.domain.service.MenuService
import org.turter.patrocl.domain.service.MessageService
import org.turter.patrocl.domain.service.OrderService
import org.turter.patrocl.domain.service.StopListService
import org.turter.patrocl.domain.service.TableService
import org.turter.patrocl.domain.service.WaiterService

@OptIn(ExperimentalOpenIdConnect::class)
val dataModule = module {
    single<HttpClient> {
        val initializer: HttpClientInitializer = get()
        initializer.defaultHttpClient
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
            employeeLocalSource = get(named("employeeLocalSource")),
            employeeService = get(),
            waiterService = get()
        )
    }

//    single<WebSocketFlowFactory> {
//        WebSocketFlowFactory(httpClient = get(), authService = get())
//    }

    single<MessageService> { MessageServiceImpl() }

    single<MenuService> {
        MenuServiceImpl(
            categoryFetcher = get(),
            modifiersFetcher = get(),
            modifiersGroupFetcher = get(),
            dishesFetcher = get(),
            stopListService = get()
        )
    }

    single<StopListService> {
        StopListServiceImpl(
            stopListApiClient = get(),
            dishFetcher = get(),
            messageService = get()
        )
    }

    single<TableService> {
        TableServiceImpl(
            tableApiClient = get(),
            tableLocalSource = get(named("tableLocalSource"))
        )
    }

    single<WaiterService> {
        WaiterServiceImpl(waiterApiClient = get(), waiterLocalRepository = get())
    }

    single<EmployeeService> {
        EmployeeServiceImpl(
            employeeApiClient = get(),
            employeeLocalSource = get(named("employeeLocalSource"))
        )
    }

    single<OrderService> { OrderServiceImpl(orderApiClient = get(), messageService = get()) }

    single<SourceApiClient> { SourceApiClientImpl(httpClient = get()) }

    single<TableApiClient> { TableApiClientImpl(httpClient = get()) }

    single<OrderApiClient> { OrderApiClientImpl(httpClient = get()) }

    single<WaiterApiClient> { WaiterApiClientImpl(httpClient = get()) }

    single<EmployeeApiClient> { EmployeeApiClientImpl(httpClient = get()) }

    single<StopListApiClient> { StopListApiClientImpl(httpClient = get()) }

    single<WaiterLocalRepository> { WaiterLocalRepositoryImpl() }

    single<LocalSource<CategoryLocal>>(named("categoryLocalSource")) { CategoryLocalSource() }

    single<LocalSource<List<DishLocal>>>(named("dishLocalSource")) { DishLocalSource() }

    single<LocalSource<ModifiersGroupLocal>>(named("modifiersGroupLocalSource")) { ModifiersGroupLocalSource() }

    single<LocalSource<List<DishModifierLocal>>>(named("modifiersLocalSource")) { DishModifierLocalSource() }

    single<LocalSource<List<TableLocal>>>(named("tableLocalSource")) { TableLocalSource() }

    single<LocalSource<EmployeeLocal>>(named("employeeLocalSource")) { EmployeeLocalSource() }

    single<CategoryFetcher> {
        CategoryFetcherImpl(
            sourceApiClient = get(),
            categoryLocalSource = get(named("categoryLocalSource"))
        )
    }

    single<DishFetcher> {
        DishFetcherImpl(
            sourceApiClient = get(),
            dishLocalSource = get(named("dishLocalSource"))
        )
    }

    single<ModifiersGroupFetcher> {
        ModifiersGroupFetcherImpl(
            sourceApiClient = get(),
            localSource = get(named("modifiersGroupLocalSource"))
        )
    }

    single<ModifiersFetcher> {
        ModifiersFetcherImpl(
            sourceApiClient = get(),
            modifiersLocalSource = get(named("modifiersLocalSource"))
        )
    }

    single<TableFetcher> {
        TableFetcherImpl(
            tableApiClient = get(),
            tableLocalSource = get(named("tableLocalSource"))
        )
    }

}