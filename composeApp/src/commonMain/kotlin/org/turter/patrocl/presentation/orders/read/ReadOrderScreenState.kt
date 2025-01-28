package org.turter.patrocl.presentation.orders.read

import org.turter.patrocl.domain.model.order.Order
import org.turter.patrocl.presentation.error.ErrorType

sealed class ReadOrderScreenState {
    data object Initial : ReadOrderScreenState()

    data object Loading : ReadOrderScreenState()

    data class Main(val order: Order) : ReadOrderScreenState()

    data class Error(val errorType: ErrorType) : ReadOrderScreenState()
}