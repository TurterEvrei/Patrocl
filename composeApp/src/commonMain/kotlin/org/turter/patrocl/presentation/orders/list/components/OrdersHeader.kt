package org.turter.patrocl.presentation.orders.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.turter.patrocl.presentation.orders.list.OrdersFilter
import org.turter.patrocl.presentation.components.SearchTextField
import org.turter.patrocl.ui.icons.Filter_alt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHeader(
    currentOrdersFilter: OrdersFilter,
    onRefreshData: () -> Unit,
    setNewFilter: (filter: OrdersFilter) -> Unit
) {
    var ordersFilterDialogOpened by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            SearchTextField(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(0.8f),
                value = currentOrdersFilter.searchName,
                onValueChange = { setNewFilter(currentOrdersFilter.copy(searchName = it)) },
                placeholder = { Text(text = "Название...") },
                textColor = MaterialTheme.colorScheme.onSurface,
                colors = OutlinedTextFieldDefaults.colors()
            )
        },
        actions = {
            IconButton(
                onClick = { ordersFilterDialogOpened = true }
            ) {
                Icon(
                    imageVector = Filter_alt,
                    contentDescription = "Filter icon"
                )
            }
            IconButton(
                onClick = onRefreshData
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh icon"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    )

//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(60.dp)
//            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(start = 12.dp, end = 12.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            SearchTextField(
//                modifier = Modifier
//                    .height(40.dp)
//                    .weight(1f),
//                value = currentOrdersFilter.searchName,
//                onValueChange = { setNewFilter(currentOrdersFilter.copy(searchName = it)) },
//                placeholder = { Text(text = "Название...") },
//                textColor = MaterialTheme.colorScheme.onSurface,
//                colors = OutlinedTextFieldDefaults.colors()
//            )
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
////                IconButton(
////                    onClick = onCreateNewOrder
////                ) {
////                    Icon(
////                        imageVector = Icons.Default.Add,
////                        contentDescription = "Add icon"
////                    )
////                }
//                IconButton(
//                    onClick = { ordersFilterDialogOpened = true }
//                ) {
//                    Icon(
//                        imageVector = Filter_alt,
//                        contentDescription = "Filter icon"
//                    )
//                }
//                IconButton(
//                    onClick = onRefreshData
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Refresh,
//                        contentDescription = "Refresh icon"
//                    )
//                }
//            }
//        }
//    }

    if (ordersFilterDialogOpened) {
        OrdersFilterDialog(
            currentFilter = currentOrdersFilter,
            onDismiss = { ordersFilterDialogOpened = false },
            onConfirm = { setNewFilter(it) }
        )
    }
}