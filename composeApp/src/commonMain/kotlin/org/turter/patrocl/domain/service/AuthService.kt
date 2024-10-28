package org.turter.patrocl.domain.service

import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.domain.model.AuthState

interface AuthService {
    fun getAuthStateFlow(): StateFlow<AuthState>
    suspend fun updateTokenIfExpired()
    suspend fun authenticate()
    suspend fun logout()
}