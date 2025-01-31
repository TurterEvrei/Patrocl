package org.turter.patrocl.data.service

import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.publicvalue.multiplatform.oidc.ExperimentalOpenIdConnect
import org.publicvalue.multiplatform.oidc.ktor.clearTokens
import org.publicvalue.multiplatform.oidc.tokenstore.OauthTokens
import org.publicvalue.multiplatform.oidc.tokenstore.TokenStore
import org.publicvalue.multiplatform.oidc.tokenstore.removeTokens
import org.publicvalue.multiplatform.oidc.tokenstore.tokensFlow
import org.publicvalue.multiplatform.oidc.types.Jwt
import org.turter.patrocl.data.auth.AppAuth
import org.turter.patrocl.data.local.LocalSource
import org.turter.patrocl.data.local.WaiterLocalRepository
import org.turter.patrocl.data.local.entity.EmployeeLocal
import org.turter.patrocl.domain.exception.AuthorizeException
import org.turter.patrocl.domain.exception.InvalidTokenException
import org.turter.patrocl.domain.exception.InvalidUserFromTokenException
import org.turter.patrocl.domain.exception.NoTokensException
import org.turter.patrocl.domain.exception.TokenExpiredException
import org.turter.patrocl.domain.model.AuthState
import org.turter.patrocl.domain.model.person.User
import org.turter.patrocl.domain.service.AuthService
import org.turter.patrocl.domain.service.EmployeeService
import org.turter.patrocl.domain.service.WaiterService

@OptIn(ExperimentalOpenIdConnect::class)
class AuthServiceImpl(
    private val appAuth: AppAuth,
    private val httpClient: HttpClient,
    private val tokenStore: TokenStore,
    private val waiterLocalRepository: WaiterLocalRepository,
    private val employeeLocalSource: LocalSource<EmployeeLocal>,
    private val employeeService: EmployeeService,
    private val waiterService: WaiterService
) : AuthService {
    private val log = Logger.withTag("AuthService")

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val tokensFlow = tokenStore.tokensFlow

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)

    init {
        coroutineScope.launch {
            log.d { "Creating auth flow" }
            tokensFlow.collect { tokens ->
                log.d { "Collecting last from tokens flow: $tokens" }
                try {
                    if (tokens != null) {
                        if (tokens.isAccessTokenValid().isSuccess) {
                            val user = tokens.extractUser()
                            tokens.permissionsValid().fold(
                                onSuccess = {
                                    when (_authState.value) {
                                        is AuthState.Authorized -> {
                                            log.d { "Access token is valid " +
                                                    "- emit AuthState.Authorized" }
                                            _authState.value = AuthState.Authorized(user)
                                        }

                                        else -> {
                                            log.d { "Current auth state is not authorize " +
                                                    "- start checking employee" }
                                            employeeService.updateEmployeeFromRemote().fold(
                                                onSuccess = { emp ->
                                                    log.d { "User employee: $emp" }
                                                    log.d { "Start checking waiter" }
                                                    waiterService.updateWaiterFromRemote().fold(
                                                        onSuccess = { waiter ->
                                                            log.d { "User waiter: $waiter" }
                                                            _authState.value =
                                                                AuthState.Authorized(user)
                                                        },
                                                        onFailure = { cause ->
                                                            log.d { "User waiter not found" }
                                                            _authState.value =
                                                                AuthState.NoBindWaiter(
                                                                    user,
                                                                    emp,
                                                                    cause
                                                                )
                                                        }
                                                    )
                                                },
                                                onFailure = { cause ->
                                                    log.d { "User employee not found" }
                                                    _authState.value =
                                                        AuthState.NoBindEmployee(user, cause)
                                                }
                                            )
                                        }
                                    }
                                },
                                onFailure = { cause ->
                                    log.d { "Permission denied for token: ${tokens.accessToken}" }
                                    _authState.value = AuthState.Forbidden(user, cause)
                                }
                            )
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
                                    _authState.value = AuthState.NotAuthorized(cause)
                                }
                            )
                        }
                    } else {
                        log.d { "Tokens from flow is null - emit AuthState.NotAuthorized" }
                        _authState.value = AuthState.NotAuthorized(NoTokensException())
                    }
                } catch (e: Exception) {
                    log.e { "Catch exception while collecting tokens from store. Exception: $e" }
                    _authState.value = AuthState.NotAuthorized(e)
                }
            }
        }
    }

    override fun getAuthStateFlow(): StateFlow<AuthState> = _authState.asStateFlow()

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

    override suspend fun authenticate(): Result<Unit> {
        log.d { "Start authentication" }
        return try {
            val res = appAuth.startAuthentication()
            tokenStore.saveTokens(
                accessToken = res.access_token,
                refreshToken = res.refresh_token,
                idToken = res.id_token
            )
            log.d { "Authentication complete, new tokens is saved. Tokens: $res" }
            Result.success(Unit)
        } catch (e: Exception) {
            log.e { "Catch exception while authenticate. Exception: $e" }
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        log.d { "Starting logout" }
        tokenStore.getIdToken()?.let { idToken ->
            log.d { "Id token: $idToken" }
            try {
                appAuth.endSession(idToken).fold(
                    onSuccess = {
                        log.d { "End session is complete" }
                    },
                    onFailure = { cause ->
                        log.e { "Fail to end session. Cause: $cause" }
                    }
                )
            } catch (e: Exception) {
                log.e { "Catch exception while end session: $e" }
            } finally {
                log.d { "Clean up local tokens, waiter and employee" }
                tokenStore.removeTokens()
                httpClient.clearTokens()
                waiterLocalRepository.cleanUp()
                employeeLocalSource.cleanUp()
                log.d { "Logout is complete" }
                return Result.success(Unit)
            }
        }
        return Result.failure(RuntimeException("No id token"))
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

    private fun OauthTokens.permissionsValid(): Result<String> {
        val parsed = Jwt.parse(accessToken)
        try {
            val realmAccess = parsed.payload
                .additionalClaims["realm_access"] as Map<String, List<Any>>
            return realmAccess.get("roles")?.let { data ->
                val roles = data.map { it.toString().removeSurrounding("\"") }.toList()
                log.d { "Roles from access token: $roles" }
                val requiredRoles = listOf(
                    "read_orders",
                    "write_orders",
                    "read_stop_list",
                    "write_stop_list"
                )
                if (roles.containsAll(requiredRoles)) {
                    log.d { "Required permissions are present" }
                    Result.success(accessToken)
                } else {
                    log.d { "Not all required permissions include" }
                    Result.failure(
                        AuthorizeException(
                            "No required permissions. Required: $requiredRoles. Current: $roles"
                        )
                    )
                }
            } ?: Result.failure(AuthorizeException("No roles in token: $accessToken"))
        } catch (e: Exception) {
            log.e { "Catching exception while parsing token roles: $e" }
            return Result.failure(e)
        }
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