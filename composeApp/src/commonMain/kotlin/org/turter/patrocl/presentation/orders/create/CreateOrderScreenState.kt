package org.turter.patrocl.presentation.orders.create

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.benasher44.uuid.Uuid
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.domain.model.menu.MenuData
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.presentation.error.ErrorType
import org.turter.patrocl.presentation.orders.common.InterceptedAddingDish

sealed class CreateOrderScreenState {
    data object Initial: CreateOrderScreenState()

    data object Loading: CreateOrderScreenState()

    data class Main(
        val menuData: MenuData,
        val tables: List<Table>,
        val ownWaiter: Waiter,
        val newOrderItems: SnapshotStateList<NewOrderItem> = mutableStateListOf(),
        val selectedTable: Table? = null,
        val selectedNewItemUuid: Uuid? = null,
        val interceptedAdding: InterceptedAddingDish? = null,
        val isSaving: Boolean = false,
        val isTablePickerOpen: Boolean = true
    ): CreateOrderScreenState() {
        fun getSelectedItem(): NewOrderItem? = newOrderItems.find { it.uuid == selectedNewItemUuid }
    }

    data class Error(val errorType: ErrorType): CreateOrderScreenState()
}