package org.turter.patrocl.presentation.orders.create.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.ui.icons.Restaurant_menu
import org.turter.patrocl.ui.icons.Table_restaurant

@Composable
fun CreateOrderTopAppBar(
    showSelectedItemBar: Boolean,
    selectedItem: NewOrderItem?,
    selectedTable: Table?,
    onBack: () -> Unit,
    onTableOpen: () -> Unit,
    onMenuOpen: () -> Unit,
    onClose: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onInfo: () -> Unit,
) {
    AnimatedContent(
        targetState = showSelectedItemBar,
        transitionSpec = {
            fadeIn() togetherWith  fadeOut() using SizeTransform(clip = false)
        }
    ) { targetState ->
        if (targetState && selectedItem != null) {
            SelectedItemTopAppBar(
                selectedItem = selectedItem,
                onClose = onClose,
                onMoveUp = onMoveUp,
                onMoveDown = onMoveDown,
                onInfo = onInfo
            )
        } else {
            DefaultTopAppBar(
                selectedTable = selectedTable,
                onTableOpen = onTableOpen,
                onMenuOpen = onMenuOpen,
                onBack = onBack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopAppBar(
    selectedTable: Table?,
    onBack: () -> Unit,
    onTableOpen: () -> Unit,
    onMenuOpen: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = "Новый заказ",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Стол: ${selectedTable?.name ?: "-"}",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
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
                onClick = onTableOpen
            ) {
                Icon(
                    imageVector = Table_restaurant,
                    contentDescription = "Table icon"
                )
            }
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
private fun SelectedItemTopAppBar(
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