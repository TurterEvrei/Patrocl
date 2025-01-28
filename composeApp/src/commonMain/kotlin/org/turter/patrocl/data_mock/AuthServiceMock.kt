package org.turter.patrocl.data_mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.AuthState
import org.turter.patrocl.domain.model.person.User
import org.turter.patrocl.domain.service.AuthService

class AuthServiceMock : AuthService {
    private val DEFAULT_USER = User(id = "user-id", username = "username")
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    
    init {
        coroutineScope.launch {
//            delay(500)
//            _authState.value = AuthState.NotAuthorized(cause = RuntimeException("Not auth"))
            _authState.value = AuthState.Authorized(user = DEFAULT_USER)
        }
    }

    override fun getAuthStateFlow(): StateFlow<AuthState> =
        _authState.asStateFlow()

    override suspend fun updateTokenIfExpired() {

    }

    override suspend fun authenticate() {
        _authState.value = AuthState.Authorized(user = DEFAULT_USER)
    }

    override suspend fun logout() {
        _authState.value = AuthState.NotAuthorized(RuntimeException("Not authorized"))
    }
}