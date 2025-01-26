package org.turter.patrocl.presentation.orders.edit

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.benasher44.uuid.Uuid
import org.turter.patrocl.domain.model.menu.MenuData
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.Order
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.presentation.error.ErrorType
import org.turter.patrocl.presentation.orders.common.InterceptedAddingDish

sealed class EditOrderScreenState {
    data object Initial : EditOrderScreenState()

    data object Loading : EditOrderScreenState()

    data class Main(
        val order: Order,
        val menuData: MenuData,
//        val tables: List<Table>,
        val ownWaiter: Waiter,
        val newOrderItems: SnapshotStateList<NewOrderItem> = mutableStateListOf(),
        val selected: Selected = Selected.None,
        val interceptedAdding: InterceptedAddingDish? = null,
        val isSaving: Boolean = false,
        val isRemoving: Boolean = false
//        var expandedOrderItemDialog: Boolean = false,
//        var orderItemForRemoving: SavedOrderItem? = null,
//        var expandedConfirmRemovingOrderItem: Boolean = false
    ) : EditOrderScreenState() {
        fun getSelectedNewItem(): NewOrderItem? = when(selected) {
            is Selected.NewItem -> newOrderItems.find { it.uuid == selected.newItemUuid }
            else -> null
        }

        fun getSingleSelectedSavedItem(): Order.Session? = getAllSelectedSavedItems()
                ?.getIfSingleItem()

        fun getAllSelectedSavedItems(): SnapshotStateList<Order.Session>? = when(selected) {
            is Selected.SavedItems -> selected.items
            else -> null
        }

        fun isAddingIntercepted() = interceptedAdding != null
    }

    data class Error(val errorType: ErrorType) : EditOrderScreenState()
}

sealed class Selected {
    data object None : Selected()
    data class NewItem(val newItemUuid: Uuid) : Selected()
    data class SavedItems(
        val items: SnapshotStateList<Order.Session> = mutableStateListOf()
    ) : Selected() {
        fun withSingleItem() = items.getIfSingleItem()
    }
}

fun List<Order.Session>.getIfSingleItem() = this.takeIf { it.size == 1 }
    ?.first()
    ?.takeIf { it.dishes.size == 1 }