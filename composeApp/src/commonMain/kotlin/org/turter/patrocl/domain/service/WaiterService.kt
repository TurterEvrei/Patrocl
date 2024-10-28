package org.turter.patrocl.domain.service

import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.domain.BindStatus
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.person.Waiter

interface WaiterService {
    fun getOwnWaiterStateFlow(): StateFlow<FetchState<Waiter>>
    fun getOwnWaiterBindStatus(): StateFlow<BindStatus>
    suspend fun checkWaiter()
    suspend fun updateWaiterFromRemote()
    suspend fun changePreferCompany(preferCompanyId: String): Result<Unit>
}