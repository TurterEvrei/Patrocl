package org.turter.patrocl.data.auth

import io.ktor.client.plugins.timeout
import io.ktor.http.encodeURLParameter
import io.ktor.http.isSuccess
import org.publicvalue.multiplatform.oidc.OpenIdConnectClient
import org.publicvalue.multiplatform.oidc.appsupport.CodeAuthFlowFactory
import org.publicvalue.multiplatform.oidc.types.remote.AccessTokenResponse
import org.turter.patrocl.domain.exception.EndSessionFailureHttpStatusCode

class AppAuth(
    private val authFlowFactory: CodeAuthFlowFactory,
    private val client: OpenIdConnectClient
) {
    private val flow = authFlowFactory.createAuthFlow(client)

    suspend fun startAuthentication(): AccessTokenResponse {
        return flow.getAccessToken()
    }

    suspend fun refreshTokens(refreshToken: String): AccessTokenResponse {
        return client.refreshToken(refreshToken = refreshToken)
    }

    suspend fun endSession(idToken: String): Result<Unit> {
        val res = client.endSession(idToken = idToken) {
            url.parameters.append("post_logout_redirect_uri", "turter.app.waiter.mobile://logout_callback".encodeURLParameter())
            timeout { requestTimeoutMillis = 3000 }
        }
        return if (res.isSuccess()) Result.success(Unit)
        else Result.failure(EndSessionFailureHttpStatusCode(res.value))
    }
}