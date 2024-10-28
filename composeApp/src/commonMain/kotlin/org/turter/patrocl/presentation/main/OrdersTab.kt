package org.turter.patrocl.presentation.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.turter.patrocl.domain.model.person.Waiter
import org.turter.patrocl.presentation.orders.list.OrdersScreen
import org.turter.patrocl.ui.icons.Table_restaurant

data class OrdersTab(
    val waiter: Waiter
): Tab {
    @Composable
    override fun Content() {
        Navigator(OrdersScreen(waiter = waiter))
    }

    override val options: TabOptions
        @Composable
        get() {
            val painterIcon = rememberVectorPainter(Table_restaurant)
            return remember {
                TabOptions(
                    index = 0u,
                    title = "Orders",
                    icon = painterIcon
                )
            }
        }
}