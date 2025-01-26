package org.turter.patrocl.data.service

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.ktor.clearTokens
import org.publicvalue.multiplatform.oidc.tokenstore.OauthTokens
import org.publicvalue.multiplatform.oidc.tokenstore.TokenStore
import org.publicvalue.multiplatform.oidc.tokenstore.removeTokens
import org.publicvalue.multiplatform.oidc.tokenstore.tokensFlow
import org.publicvalue.multiplatform.oidc.types.Jwt
import org.turter.patrocl.data.auth.AppAuth
import org.turter.patrocl.data.local.entity.EmployeeLocal
import org.turter.patrocl.domain.exception.InvalidTokenException
import org.turter.patrocl.domain.exception.InvalidUserFromTokenException
import org.turter.patrocl.domain.exception.NoTokensException
import org.turter.patrocl.domain.exception.TokenExpiredException
import org.turter.patrocl.domain.model.AuthState
import org.turter.patrocl.domain.model.person.User
import org.turter.patrocl.data.local.LocalSource
import org.turter.patrocl.data.local.WaiterLocalRepository
import org.turter.patrocl.domain.service.AuthService

@OptIn(ExperimentalOpenIdConnect::class)
class AuthServiceImpl(
    private val appAuth: AppAuth,
    private val httpClient: HttpClient,
    private val tokenStore: TokenStore,
    private val waiterLocalRepository: WaiterLocalRepository,
    private val employeeLocalSource: LocalSource<EmployeeLocal>
) : AuthService {
    private val log = Logger.withTag("AuthService")

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val tokensFlow = tokenStore.tokensFlow

    private val authStateFlow = flow<AuthState> {
        log.d { "Creating auth flow" }
        tokensFlow.collect { tokens ->
            log.d { "Collecting last from tokens flow: $tokens" }
            try {
                if (tokens != null) {
                    if (tokens.isAccessTokenValid().isSuccess) {
                        log.d { "Access token is valid - emit AuthState.Authorized" }
                        emit(AuthState.Authorized(tokens.extractUser()))
                    } else {
                        tokens.isRefreshTokenValid().fold(
                            onSuccess = { refreshToken ->
                                log.d { "Refresh token is valid - start refreshing tokens" }
                                refreshTokens(refreshToken)
                            },
                            onFailure = { cause ->
                                log.d {
                                    "Refresh token is invalid - emit AuthState.NotAuthorized. " +
                                            "Cause: $cause"
                                }
                                emit(AuthState.NotAuthorized(cause))
                            }
                        )
                    }
                } else {
                    log.d { "Tokens from flow is null - emit AuthState.NotAuthorized" }
                    emit(AuthState.NotAuthorized(NoTokensException()))
                }
            } catch (e: Exception) {
                log.e { "Catch exception while collecting tokens from store. Exception: $e" }
                emit(AuthState.NotAuthorized(e))
            }
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = AuthState.Initial
    )

    override fun getAuthStateFlow(): StateFlow<AuthState> = authStateFlow

    override suspend fun updateTokenIfExpired() {
        tokensFlow.first()?.let { tokens ->
            log.d { "Updating tokens if needed - tokens are present" }
            tokens.isAccessTokenValid().onFailure {
                log.d { "Updating tokens if needed - access token expired, needs refreshing" }
                tokens.isRefreshTokenValid().onSuccess { refreshToken ->
                    log.d { "Updating tokens if needed - calling refresh" }
                    refreshTokens(refreshToken)
                }
            }
        }
    }

    override suspend fun authenticate() {
        log.d { "Start authentication" }
        try {
            val res = appAuth.startAuthentication()
            tokenStore.saveTokens(
                accessToken = res.access_token,
                refreshToken = res.refresh_token,
                idToken = res.id_token
            )
            log.d { "Authentication complete, new tokens is saved. Tokens: $res" }
        } catch (e: Exception) {
            log.e { "Catch exception while authenticate. Exception: $e" }
            e.printStackTrace()
        }
    }

    override suspend fun logout() {
        log.d { "Starting logout" }
        tokenStore.getIdToken()?.let { idToken ->
            log.d { "Id token: $idToken" }
            appAuth.endSession(idToken).fold(
                onSuccess = {
                    httpClient.clearTokens()
                    tokenStore.removeTokens()
                    waiterLocalRepository.cleanUp()
                    employeeLocalSource.cleanUp()
                    log.d { "Logout is complete" }
                },
                onFailure = { cause ->
                    log.e { "Fail to logout. Cause: $cause" }
                }
            )
        }
    }

    private suspend fun refreshTokens(refreshToken: String) {
        log.d { "Start refreshing tokens" }
        try {
            val res = appAuth.refreshTokens(refreshToken)
            log.d { "Result of tokens refreshing: $res" }
            tokenStore.saveTokens(
                accessToken = res.access_token,
                refreshToken = res.refresh_token,
                idToken = res.id_token
            )
        } catch (e: Exception) {
            log.e { "Catching exception while refreshing tokens - $e" }
            log.d { "Start removing tokens" }
            tokenStore.removeTokens()
        }
    }

    private fun OauthTokens.isAccessTokenValid(): Result<String> = isTokenValid(accessToken)

    private fun OauthTokens.isRefreshTokenValid(): Result<String> =
        refreshToken?.let { isTokenValid(it) } ?: Result.failure(NullPointerException())

    private fun isTokenValid(token: String): Result<String> {
        val exp =
            Jwt.parse(token).payload.exp ?: return Result.failure(InvalidTokenException(token))
        val currentTime = Clock.System.now().epochSeconds
        val res = currentTime < exp
        log.d { "Check token is valid, resul: $res. \n Exp: $exp \n Current: $currentTime" }
        return if (res) Result.success(token) else Result.failure(
            TokenExpiredException(
                exp = exp,
                cur = currentTime
            )
        )
    }

    private fun OauthTokens.extractUser(): User =
        accessToken.let {
            val payload = Jwt.parse(it).payload
            val id = payload.sub
            val username = payload.additionalClaims["preferred_username"] as String?
            if (id != null && username != null) {
                log.d { "Success extract user from token. Id: $id. Username: $username" }
                User(id = id, username = username)
            } else {
                log.e { "Fail to extract user from token. Id: $id. Username: $username" }
                throw InvalidUserFromTokenException(it)
            }
        }
}