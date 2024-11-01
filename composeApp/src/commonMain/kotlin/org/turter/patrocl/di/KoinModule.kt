package org.turter.patrocl.di

import androidx.compose.runtime.Composable
import org.koin.compose.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.tokenstore.TokenStore
import org.turter.patrocl.data.auth.AppAuth
import org.turter.patrocl.data.auth.OidcClientInitializer
import org.turter.patrocl.data.client.HttpClientInitializer


@OptIn(ExperimentalOpenIdConnect::class)
val appModule = module {
    single { OidcClientInitializer() }
    factory {
        HttpClientInitializer(
            authClient = get(),
            tokenStore = get()
        )
    }
    factory {
        AppAuth(
            authFlowFactory = get(),
            client = get()
        )
    }
}

@OptIn(ExperimentalOpenIdConnect::class)
fun initKoin(
    authFlowFactory: CodeAuthFlowFactory,
    tokenStore: TokenStore,
    config: KoinAppDeclaration? = null
) = startKoin {
    config?.invoke(this)
    modules(
        module {
            single<CodeAuthFlowFactory> { authFlowFactory }
            single<TokenStore> { tokenStore }
        },
        appModule,
        viewModelModule,
        dataModule
    )
}

@OptIn(ExperimentalOpenIdConnect::class)
@Composable
fun KoinApplication(
    authFlowFactory: CodeAuthFlowFactory,
    tokenStore: TokenStore,
    content: @Composable () -> Unit
) {
    KoinApplication(
        application = {
            modules(
                module {
                    single { authFlowFactory }
                    single { tokenStore }
                },
                appModule,
                viewModelModule,
                dataModule
            )
        },
        content = content
    )
}