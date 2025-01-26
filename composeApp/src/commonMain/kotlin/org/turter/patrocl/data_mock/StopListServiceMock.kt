package org.turter.patrocl.data_mock

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDateTime
import org.turter.patrocl.data_mock.utils.StopListDataSupplier
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.Message
import org.turter.patrocl.domain.model.stoplist.NewStopListItem
import org.turter.patrocl.domain.model.stoplist.StopList
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.domain.service.MessageService
import org.turter.patrocl.domain.service.StopListService
import kotlin.random.Random

class StopListServiceMock(
    private val messageService: MessageService
): StopListService {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val randomIntFlow = flow {
        while (true) {
            emit(Random.nextInt(1, 5))
            delay(5000)
        }
    }

    private val stopListFlow = flow {
        emit(FetchState.loading())
        delay(300)
        randomIntFlow.collect { i ->
            emit(
                FetchState.success(
                    if (i % 2 == 0) StopListDataSupplier.getStopList()
                    else StopListDataSupplier.getStopListMutated()
                )
            )
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

    override fun getStopListStateFlow(): StateFlow<FetchState<StopList.Success>> =
        stopListFlow

    override suspend fun refreshStopList() {

    }

    override suspend fun createNewItem(item: NewStopListItem): Result<Unit> {
        delay(300)
        messageService.setMessage(Message.success("New item created"))
        return Result.success(Unit)
    }

    override suspend fun editItem(
        id: String,
        remainingCount: Int,
        until: LocalDateTime?
    ): Result<Unit> {
        delay(300)
        messageService.setMessage(Message.success("Item updated"))
        return Result.success(Unit)
    }

    override suspend fun removeItem(id: String): Result<Unit> {
        delay(300)
        messageService.setMessage(Message.success("Item deleted"))
        return Result.success(Unit)
    }

    override suspend fun removeItems(ids: List<String>): Result<Unit> {
        delay(300)
        messageService.setMessage(Message.success("Items deleted"))
        return Result.success(Unit)
    }
}