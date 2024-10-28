package org.turter.patrocl.presentation.orders.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.turter.patrocl.domain.model.order.OrdersFilter
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.error.ErrorComponent
import org.turter.patrocl.presentation.orders.create.CreateOrderScreen
import org.turter.patrocl.presentation.orders.edit.EditOrderScreen
import org.turter.patrocl.presentation.orders.list.components.OrderCard
import org.turter.patrocl.presentation.orders.list.components.OrdersFilterDialog
import org.turter.patrocl.ui.icons.Filter_alt

class OrdersScreen(
    private val waiter: Waiter
) : Screen {
    @Composable
    override fun Content() {
        val vm: OrdersViewModel = getScreenModel()
        val navigator = LocalNavigator.currentOrThrow

        var ordersFilter by remember { mutableStateOf(OrdersFilter()) }
        var ordersFilterDialogOpened by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 24.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Заказы:",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = { navigator.push(CreateOrderScreen()) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add icon"
                            )
                        }
                        IconButton(
                            onClick = { ordersFilterDialogOpened = true }
                        ) {
                            Icon(
                                imageVector = Filter_alt,
                                contentDescription = "Filter icon"
                            )
                        }
                        IconButton(
                            onClick = { TODO() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh icon"
                            )
                        }
                    }
                }
            }

            when (val currentState = vm.screenState.collectAsState().value) {
                is OrdersScreenState.Content -> {
                    val orders = ordersFilter.filter(orders = currentState.orders, waiter = waiter)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(
                            horizontal = 8.dp,
                            vertical = 12.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = orders,
                            key = { it.guid }
                        ) { order ->
                            OrderCard(
                                order = order,
                                onCardClick = {
                                    navigator.push(EditOrderScreen(orderGuid = order.guid))
                                }
                            )
                        }
                    }

                    if (ordersFilterDialogOpened) {
                        OrdersFilterDialog(
                            currentFilter = ordersFilter,
                            onDismiss = { ordersFilterDialogOpened = false },
                            onConfirm = { newFilter -> ordersFilter = newFilter }
                        )
                    }
                }

                is OrdersScreenState.Error -> {
                    ErrorComponent(
                        error = currentState.errorType,
                        onRetry = { vm.sendEvent(OrdersUiEvent.RefreshOrders) }
                    )
                }

                else -> {
                    CircularLoader()
                }
            }
        }

    }
}