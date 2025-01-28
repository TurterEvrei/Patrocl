package org.turter.patrocl.presentation.orders.item.new.edit

import cafe.adriel.voyager.core.model.ScreenModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.turter.patrocl.domain.model.menu.DishDetailed
import org.turter.patrocl.domain.model.menu.DishModifier
import org.turter.patrocl.domain.model.menu.MenuData
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.presentation.orders.common.AddingWarningType
import org.turter.patrocl.presentation.orders.common.InterceptedAddingDish

sealed class EditNewOrderItemUiEvent {
    data class SelectDish(val dish: DishDetailed) : EditNewOrderItemUiEvent()
    data class SetQuantity(val quantity: Float) : EditNewOrderItemUiEvent()
    data class SelectTargetModifier(val modifier: DishModifier) : EditNewOrderItemUiEvent()
    data class SetTargetModifierQuantity(val quantity: Int) : EditNewOrderItemUiEvent()
    data object AddTargetModifier : EditNewOrderItemUiEvent()
    data class AddComment(val comment: String) : EditNewOrderItemUiEvent()
    data class RemoveModifier(val modifier: NewOrderItem.Modifier) : EditNewOrderItemUiEvent()
    data class ConfirmInterceptedAddingAndThen(val action: () -> Unit) : EditNewOrderItemUiEvent()
    data object RejectInterceptedAdding : EditNewOrderItemUiEvent()
    data class SaveChangesAndThen(val action: () -> Unit) : EditNewOrderItemUiEvent()
    data class DeleteItemAndThen(val action: () -> Unit) : EditNewOrderItemUiEvent()
}

class EditNewOrderItemViewModel(
    private val originalItem: NewOrderItem,
    private val menuData: MenuData,
    private val onSave: (item: NewOrderItem) -> Unit,
    private val onDelete: (item: NewOrderItem) -> Unit
) : ScreenModel {
    private val log = Logger.withTag("EditNewOrderItemViewModel")

    private val _screenState = MutableStateFlow(
        EditNewOrderItemScreenState(
            originalItem = originalItem,
            dish = menuData.dishes.first { it.id == originalItem.dishId },
            quantity = originalItem.quantity,
            modifiers = originalItem.modifiers
        )
    )

    val screenState: StateFlow<EditNewOrderItemScreenState> = _screenState.asStateFlow()

    fun sendEvent(event: EditNewOrderItemUiEvent) {
        when (event) {
            is EditNewOrderItemUiEvent.SelectDish -> selectDish(event.dish)
            is EditNewOrderItemUiEvent.SetQuantity -> setQuantity(event.quantity)
            is EditNewOrderItemUiEvent.SelectTargetModifier -> selectTargetModifier(event.modifier)
            is EditNewOrderItemUiEvent.SetTargetModifierQuantity -> setTargetModifierQuantity(event.quantity)
            is EditNewOrderItemUiEvent.AddTargetModifier -> addTargetModifier()
            is EditNewOrderItemUiEvent.AddComment -> addComment(event.comment)
            is EditNewOrderItemUiEvent.RemoveModifier -> removeModifier(event.modifier)
            is EditNewOrderItemUiEvent.ConfirmInterceptedAddingAndThen -> confirmInterceptedAdding(event.action)
            is EditNewOrderItemUiEvent.RejectInterceptedAdding -> cleanInterceptedAdding()
            is EditNewOrderItemUiEvent.SaveChangesAndThen -> saveChanges(event.action)
            is EditNewOrderItemUiEvent.DeleteItemAndThen -> deleteItem(event.action)
        }
    }

    private fun selectDish(dish: DishDetailed) {
        transformState { it.copy(dish = dish) }
    }

    private fun setQuantity(quantity: Float) {
        if (quantity > 0) transformState { it.copy(quantity = quantity) }
    }

    private fun selectTargetModifier(modifier: DishModifier) {
        transformState { it.copy(targetModifier = modifier) }
    }

    private fun setTargetModifierQuantity(quantity: Int) {
        if (quantity > 0) transformState { it.copy(targetModifierQuantity = quantity) }
    }

    private fun addTargetModifier() {
        withState { state ->
            val modifier = state.targetModifier
            val quantity = state.targetModifierQuantity
            if (modifier != null && quantity > 0) {
                transformState {
                    it.copy(
                        modifiers = it.modifiers.toMutableList().apply {
                            find { mod -> mod.modifierId == modifier.id }
                                ?.let { savedMod ->
                                    set(
                                        indexOf(savedMod),
                                        savedMod.copy(quantity = savedMod.quantity + quantity)
                                    )
                                }
                                ?: add(
                                    NewOrderItem.Modifier.regular(
                                        modifierId = modifier.id,
                                        name = modifier.name,
                                        quantity = quantity
                                    )
                                )
                        },
                        targetModifier = null,
                        targetModifierQuantity = 1
                    )
                }
            }
        }
    }

    private fun addComment(comment: String) {
        transformState {
            it.copy(
                modifiers = it.modifiers.toMutableList().apply {
                    find { mod ->
                        mod.type == NewOrderItem.Modifier.Type.COMMENT && mod.content == comment
                    }
                        ?: add(NewOrderItem.Modifier.comment(comment))
                }.toList()
            )
        }
    }

    private fun removeModifier(modifier: NewOrderItem.Modifier) {
        transformState {
            it.copy(modifiers = it.modifiers.toMutableList().apply { remove(modifier) }.toList())
        }
    }

    private fun confirmInterceptedAdding(action: () -> Unit) {
        withState { state ->
            state.interceptedAdding?.let { intercepted ->
                onSave(intercepted.target)
                action()
            }
        }
    }

    private fun cleanInterceptedAdding() = transformState { it.copy(interceptedAdding = null) }

    private fun saveChanges(action: () -> Unit) {
        withState { state ->
            val itemToSave = originalItem.copy(
                dishId = state.dish.id,
                dishName = state.dish.name,
                quantity = state.quantity,
                modifiers = state.modifiers
            )
            val onStop = state.dish.onStop
            val remainingCount = state.dish.remainingCount

            if (onStop || remainingCount - state.quantity < 5) {
                transformState {
                    it.copy(
                        interceptedAdding = InterceptedAddingDish(
                            target = itemToSave,
                            warningType = AddingWarningType.of(onStop, remainingCount)
                        )
                    )
                }
            } else {
                onSave(itemToSave)
                action()
            }
        }
    }

    private fun deleteItem(action: () -> Unit) {
        onDelete(originalItem)
        action()
    }

    private fun transformState(
        action: (EditNewOrderItemScreenState) -> EditNewOrderItemScreenState
    ) {
        _screenState.value = action(_screenState.value)
    }

    private fun withState(action: (EditNewOrderItemScreenState) -> Unit) {
        _screenState.value.let(action)
    }
}