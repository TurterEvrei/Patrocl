package org.turter.patrocl.data.service

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import org.turter.patrocl.data.mapper.stoplist.toCreateStopListItemPayload
import org.turter.patrocl.data.mapper.stoplist.toEditStopListItemPayload
import org.turter.patrocl.data.mapper.stoplist.toStopList
import org.turter.patrocl.data.remote.client.StopListApiClient
import org.turter.patrocl.domain.fetcher.DishFetcher
import org.turter.patrocl.domain.model.FetchState
import org.turter.patrocl.domain.model.Message
import org.turter.patrocl.domain.model.stoplist.NewStopListItem
import org.turter.patrocl.domain.model.stoplist.StopList
import org.turter.patrocl.domain.service.MessageService
import org.turter.patrocl.domain.service.StopListService

class StopListServiceImpl(
    private val stopListApiClient: StopListApiClient,
    private val dishFetcher: DishFetcher,
    private val messageService: MessageService
): StopListService {
    private val log = Logger.withTag("StopListServiceImpl")
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val dishes = dishFetcher.getStateFlow()

    private val checkStopListFlow = MutableSharedFlow<Unit>(replay = 1)

//    private val stopListStateFlow =
//        MutableStateFlow<FetchState<StopList.Success>>(FetchState.initial())

    private val stopListStateFlow = flow<FetchState<StopList.Success>> {
        checkStopListFlow.emit(Unit)
        combine(
            checkStopListFlow,
            stopListApiClient.getStopListFlow(),
            dishes
        ) { _, stopListResult, dishesFetchState ->
            log.d("Start stop list flow")
            try {
                val stopList = stopListResult.getOrThrow()
                val dishes = dishesFetchState.takeIfSuccess()
                log.d("Fetched resources for stop list: \n" +
                        " - stop list: $stopList \n" +
                        " - dishes: $dishes")
                if (dishes != null) {
                    stopList.toStopList(dishes).let { list ->
                        when(list) {
                            is StopList.Success -> {
                                log.d("Return success fetch state of stop list: $list")
                                FetchState.success(list)
                            }
                            is StopList.Error -> {
                                log.d(
                                    "Return fail fetch state of stop list - ${list.message}"
                                )
                                FetchState.fail(RuntimeException(list.message))
                            }
                        }
                    }
                } else {
                    log.d("Return loading fetch state of stop list")
                    FetchState.loading()
                }
            } catch (e: Exception) {
                log.e { "Catch exception while collecting stop list flow. Exception: $e" }
                FetchState.fail(e)
            }
        }.collect { newState ->
            log.d("Emit new state of stop list: $newState")
            emit(newState)
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.Lazily,
        initialValue = FetchState.initial()
    )

//    init {
//        coroutineScope.launch {
//            combine(
//                checkStopListFlow,
//                stopListApiClient.getStopListFlow(),
//                dishes
//            ) { _, stopListResult, dishesFetchState ->
//                try {
//                    val stopList = stopListResult.getOrThrow()
//                    val dishes = dishesFetchState.takeIfSuccess()
//                    if (dishes != null) {
//                        stopList.toStopList(dishes).let { list ->
//                            when(list) {
//                                is StopList.Success -> FetchState.success(list)
//                                is StopList.Error -> FetchState.fail(RuntimeException(list.message))
//                            }
//                        }
//                    } else {
//                        FetchState.loading()
//                    }
//                } catch (e: Exception) {
//                    log.e { "Catch exception while collecting stop list flow. Exception: $e" }
//                    FetchState.fail(e)
//                }
//            }.collect { newState ->
//                stopListStateFlow.value = newState
//            }
//        }
//    }

    override fun getStopListStateFlow(): StateFlow<FetchState<StopList.Success>> =
        stopListStateFlow

    override suspend fun refreshStopList() {
        checkStopListFlow.emit(Unit)
    }

    override suspend fun createNewItem(item: NewStopListItem): Result<Unit> {
        return stopListApiClient
            .createItem(payload = item.toCreateStopListItemPayload())
            .fold(
                onSuccess = {
                    log.d { "Success creation item: $it" }
                    messageService.setMessage(Message.success("Success updating item"))
                    Result.success(Unit)
                }, onFailure = {
                    log.e { "Failure creation item: $it" }
                    messageService.setMessage(Message.error(it))
                    Result.failure(it)
                }
            )
    }

    override suspend fun editItem(
        id: String,
        remainingCount: Int,
        until: LocalDateTime?
    ): Result<Unit> {
        return stopListApiClient
            .editItem(payload = toEditStopListItemPayload(
                id = id,
                remainingCount = remainingCount,
                until = until
            ))
            .fold(
                onSuccess = {
                    log.d { "Success updating item: $it" }
                    messageService.setMessage(Message.success("Success updating item"))
                    Result.success(Unit)
                }, onFailure = {
                    log.e { "Failure updating item: $it" }
                    messageService.setMessage(Message.error(it))
                    Result.failure(it)
                }
            )
    }

    override suspend fun removeItem(id: String): Result<Unit> {
        return stopListApiClient
            .deleteItem(id)
            .fold(
                onSuccess = {
                    log.d { "Success removing item: $id" }
                    messageService.setMessage(Message.success("Success removing item"))
                    Result.success(Unit)
                }, onFailure = {
                    log.e { "Failure removing item: $it" }
                    messageService.setMessage(Message.error(it))
                    Result.failure(it)
                }
            )
    }

    override suspend fun removeItems(ids: List<String>): Result<Unit> {
        return stopListApiClient
            .deleteItems(ids)
            .fold(
                onSuccess = {
                    log.d { "Success removing items: $ids" }
                    messageService.setMessage(Message.success("Success removing items"))
                    Result.success(Unit)
                }, onFailure = {
                    log.e { "Failure removing items: $it" }
                    messageService.setMessage(Message.error(it))
                    Result.failure(it)
                }
            )
    }
}