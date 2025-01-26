package org.turter.patrocl.data_mock

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.turter.patrocl.data_mock.utils.TableDataSupplier
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.domain.service.TableService

class TableServiceMock: TableService {

    private val tableFlow = MutableStateFlow(FetchState.success(TableDataSupplier.getTables()))
    private val tableDataStatusFlow = MutableStateFlow<DataStatus>(DataStatus.Ready)

    override fun getTablesStateFlow(): StateFlow<FetchState<List<Table>>> =
        tableFlow

    override fun getTablesDataStatusStateFlow(): StateFlow<DataStatus> =
        tableDataStatusFlow

    override suspend fun refreshTables() {
        tableDataStatusFlow.value = DataStatus.Loading
        delay(300)
        tableDataStatusFlow.value = DataStatus.Ready
        tableFlow.value = FetchState.success(TableDataSupplier.getTables())
    }

    override suspend fun refreshTablesFromApi() {
        tableDataStatusFlow.value = DataStatus.Loading
        delay(300)
        tableDataStatusFlow.value = DataStatus.Ready
        tableFlow.value = FetchState.success(TableDataSupplier.getTables())
    }
}