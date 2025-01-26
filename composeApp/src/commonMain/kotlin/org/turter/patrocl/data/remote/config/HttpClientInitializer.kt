package org.turter.patrocl.data.remote.config

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.ktor.oidcBearer
import org.publicvalue.multiplatform.oidc.tokenstore.TokenRefreshHandler
import org.publicvalue.multiplatform.oidc.tokenstore.TokenStore

@OptIn(ExperimentalOpenIdConnect::class)
class HttpClientInitializer(
    private val authClient: OpenIdConnectClient,
    private val tokenStore: TokenStore
) {
    private val refreshHandler = TokenRefreshHandler(tokenStore = tokenStore)

    val defaultHttpClient = HttpClient(getKtorEngine()) {
        install(Auth) {
            oidcBearer(
                tokenStore = tokenStore,
                client = authClient,
                refreshHandler = refreshHandler
            )
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(SSE) {
            showCommentEvents()
            showRetryEvents()
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15_000
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = Logger.SIMPLE
        }
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
//            pingInterval = 500
            maxFrameSize = Long.MAX_VALUE
        }
    }

}

expect fun getKtorEngine(): HttpClientEngine