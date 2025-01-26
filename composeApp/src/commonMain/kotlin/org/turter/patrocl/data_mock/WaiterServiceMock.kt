package org.turter.patrocl.data_mock

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.turter.patrocl.data_mock.utils.WaiterDataSupplier
import org.turter.patrocl.domain.model.BindStatus
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.domain.service.WaiterService

class WaiterServiceMock: WaiterService {

    private val waiterFetchState = FetchState.success(WaiterDataSupplier.getWaiter())

    private val waiterFlow = MutableStateFlow(waiterFetchState)

    private val waiterBindStatus = MutableStateFlow<BindStatus>(BindStatus.Bind)

    override fun getOwnWaiterStateFlow(): StateFlow<FetchState<Waiter>> =
        waiterFlow

    override fun getOwnWaiterBindStatus(): StateFlow<BindStatus> =
        waiterBindStatus.asStateFlow()

    override suspend fun checkWaiter() {
        waiterBindStatus.value = BindStatus.Loading
        delay(300)
        waiterFlow.value = waiterFetchState
    }

    override suspend fun updateWaiterFromRemote() {
        waiterBindStatus.value = BindStatus.Loading
        delay(300)
        waiterFlow.value = waiterFetchState    }
}