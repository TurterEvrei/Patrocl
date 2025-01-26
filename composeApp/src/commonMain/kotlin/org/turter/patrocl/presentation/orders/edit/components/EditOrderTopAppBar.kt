package org.turter.patrocl.presentation.orders.edit.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.ui.icons.Restaurant_menu
import org.turter.patrocl.utils.toFormattedString

sealed class EditOrderTopAppBarState {
    data class Default(
        val orderName: String,
        val waiterName: String,
//        val sum: Float,
        val onBack: () -> Unit,
        val onMenuOpen: () -> Unit
    ) : EditOrderTopAppBarState()

    data class NewItemSelected(
        val selectedItem: NewOrderItem,
        val onClose: () -> Unit,
        val onMoveUp: () -> Unit,
        val onMoveDown: () -> Unit,
        val onInfo: () -> Unit
    ) : EditOrderTopAppBarState()

    data class SavedItemsSelected(
        val selectedItemsCount: Int,
        val onClose: () -> Unit,
        val onDelete: () -> Unit
    ) : EditOrderTopAppBarState()

    data class SingleSavedItemSelected(
        val itemName: String,
        val itemCount: Float,
        val onClose: () -> Unit,
        val onDelete: () -> Unit
    ) : EditOrderTopAppBarState()
}

@Composable
fun EditOrderTopAppBar(
    state: EditOrderTopAppBarState
) {
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn() togetherWith fadeOut() using SizeTransform(clip = false)
        }
    ) { targetState ->
        when (targetState) {
            is EditOrderTopAppBarState.Default -> DefaultTopAppBar(
                orderName = targetState.orderName,
                waiterName = targetState.waiterName,
//                sum = targetState.sum,
                onMenuOpen = targetState.onMenuOpen,
                onBack = targetState.onBack
            )

            is EditOrderTopAppBarState.NewItemSelected -> SelectedNewItemTopAppBar(
                selectedItem = targetState.selectedItem,
                onClose = targetState.onClose,
                onMoveUp = targetState.onMoveUp,
                onMoveDown = targetState.onMoveDown,
                onInfo = targetState.onInfo
            )

            is EditOrderTopAppBarState.SavedItemsSelected -> SavedItemsSelectedTopAppBar(
                count = targetState.selectedItemsCount,
                onClose = targetState.onClose,
                onDelete = targetState.onDelete
            )

            is EditOrderTopAppBarState.SingleSavedItemSelected -> SingleSelectedSavedItemTopAppBar(
                itemName = targetState.itemName,
                itemCount = targetState.itemCount,
                onClose = targetState.onClose,
                onDelete = targetState.onDelete
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopAppBar(
    orderName: String,
    waiterName: String,
//    sum: Float,
    onBack: () -> Unit,
    onMenuOpen: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Заказ: $orderName",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Официант: $waiterName",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
//                Row {
//                    Spacer(Modifier.width(8.dp))
//                    Text(
//                        text = "Сумма: ${sum.toFormattedString(2)}",
//                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
//                    )
//                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back icon"
                )
            }
        },
        actions = {
            IconButton(
                onClick = onMenuOpen
            ) {
                Icon(
                    imageVector = Restaurant_menu,
                    contentDescription = "Menu icon"
                )
            }
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectedNewItemTopAppBar(
    selectedItem: NewOrderItem,
    onClose: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onInfo: () -> Unit,
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = selectedItem.dishName,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = "Кол-во: ${selectedItem.quantity}",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close icon"
                )
            }
        },
        actions = {
            IconButton(onClick = onMoveUp) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Arrow up icon"
                )
            }
            IconButton(onClick = onMoveDown) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Arrow down icon"
                )
            }
            IconButton(onClick = onInfo) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Info icon"
                )
            }
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedItemsSelectedTopAppBar(
    count: Int,
    onClose: () -> Unit,
    onDelete: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "Выбрано: $count",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close icon"
                )
            }
        },
        actions = {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete icon"
                )
            }
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SingleSelectedSavedItemTopAppBar(
    itemName: String,
    itemCount: Float,
    onClose: () -> Unit,
    onDelete: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = itemName,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = "Всего: $itemCount",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close icon"
                )
            }
        },
        actions = {
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete icon"
                )
            }
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    )
}








