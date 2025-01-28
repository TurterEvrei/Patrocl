package org.turter.patrocl.presentation.orders.read

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.navigator.Navigator
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.FetchState.Finished
import org.turter.patrocl.domain.service.OrderService
import org.turter.patrocl.presentation.error.ErrorType
import org.turter.patrocl.presentation.orders.read.ReadOrderUiEvent.BackToOrders

sealed class ReadOrderUiEvent {
    data object BackToOrders : ReadOrderUiEvent()
    data object RefreshOrder : ReadOrderUiEvent()
}
class ReadOrderViewModel(
    private val orderGuid: String,
    private val navigator: Navigator,
    private val orderService: OrderService
): ScreenModel {
    private val log = Logger.withTag("ReadOrderViewModel")

    private val coroutineScope = screenModelScope

    private val _screenState = MutableStateFlow<ReadOrderScreenState>(ReadOrderScreenState.Initial)

    val screenState = _screenState.asStateFlow()

    init {
        coroutineScope.launch {
            orderService.getOrderFlow(orderGuid).collect { orderFetchState ->
                log.d { "Read order screen: collect order flow: $orderFetchState"}
                _screenState.value = if (orderFetchState is Finished) {
                    try {
                        val orderData = orderFetchState.result.getOrThrow()
                        ReadOrderScreenState.Main(order = orderData)
                    } catch (e: Exception) {
                        log.e { "Catch exception in combine flows: $e" }
                        e.printStackTrace()
                        ReadOrderScreenState.Error(errorType = ErrorType.from(e))
                    }
                } else {
                    ReadOrderScreenState.Loading
                }
            }
        }
    }

    fun sendEvent(event: ReadOrderUiEvent) {
        when(event) {
            is BackToOrders -> navigator.popUntilRoot()
            is ReadOrderUiEvent.RefreshOrder -> refreshOrder()
        }
    }

    private fun refreshOrder() {
        _screenState.value = ReadOrderScreenState.Loading
        coroutineScope.launch {
            orderService.refreshCurrentOrder()
        }
    }
}