package org.turter.patrocl.presentation.orders.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.presentation.components.CircularLoader
import org.turter.patrocl.presentation.error.ErrorComponent
import org.turter.patrocl.presentation.orders.create.CreateOrderScreen
import org.turter.patrocl.presentation.orders.edit.EditOrderScreen
import org.turter.patrocl.presentation.orders.list.components.OrderCard
import org.turter.patrocl.presentation.orders.list.components.OrdersHeader
import org.turter.patrocl.presentation.orders.read.ReadOrderScreen

class OrdersScreen(
    private val waiter: Waiter
) : Screen {
    @Composable
    override fun Content() {
        val vm: OrdersViewModel = koinScreenModel()
        val navigator = LocalNavigator.currentOrThrow

        var ordersFilter by remember { mutableStateOf(OrdersFilter()) }

        Scaffold(
            topBar = {
                OrdersHeader(
                    currentOrdersFilter = ordersFilter,
                    onRefreshData = { vm.sendEvent(OrdersUiEvent.RefreshOrders) },
                    setNewFilter = { newFilter -> ordersFilter = newFilter }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(CreateOrderScreen()) }
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add icon")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
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
                                    modifier = Modifier.animateItem(),
                                    order = order,
                                    onCardClick = {
                                        val orderGuid = order.guid
                                        navigator.push(
                                            if (order.bill) ReadOrderScreen(orderGuid = orderGuid)
                                            else EditOrderScreen(orderGuid = orderGuid)
                                        )
                                    }
                                )
                            }
                            item {
                                Spacer(modifier = Modifier.height(88.dp))
                            }
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
}