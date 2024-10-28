package org.turter.patrocl.presentation.orders.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.domain.model.order.OrderItem
import org.turter.patrocl.domain.model.order.SavedOrderItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderItemCard(
    modifier: Modifier = Modifier,
    item: OrderItem,
    enabled: Boolean = true,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .combinedClickable(
                enabled = enabled,
                onLongClick = onLongClick,
                onClick = onClick
            )
            .fillMaxSize(),
        colors = when(item) {
            is SavedOrderItem -> CardDefaults.cardColors().copy(
                contentColor = CardDefaults.cardColors().disabledContentColor,
                containerColor = CardDefaults.cardColors().disabledContainerColor
            )
            is NewOrderItem -> CardDefaults.cardColors()
            else -> CardDefaults.cardColors()
        },
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = Modifier.padding(10.dp, 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                item.dishName,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "x${item.quantity}"
            )
        }
        item.modifiers.map { mod ->
            Row(
                modifier = Modifier.padding(20.dp, 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = " - ${mod.name} x${mod.quantity}")
            }
        }
    }
}

