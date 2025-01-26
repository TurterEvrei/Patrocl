package org.turter.patrocl.data_mock.fetcher

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import org.turter.patrocl.data_mock.utils.MenuDataSupplier
import org.turter.patrocl.domain.fetcher.DishFetcher
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.menu.Dish

class DishFetcherMock: DishFetcher {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    override fun getStateFlow(): StateFlow<FetchState<List<Dish>>> {
        return flowOf(FetchState.success(MenuDataSupplier.getDishList()))
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.Lazily,
                initialValue = FetchState.initial()
            )
    }

    override fun getDataStatus(): StateFlow<DataStatus> {
        return MutableStateFlow<DataStatus>(DataStatus.Ready).asStateFlow()
    }

    override suspend fun refresh() {

    }

    override suspend fun refreshFromRemote() {

    }
}