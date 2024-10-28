package org.turter.patrocl.presentation.orders.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.order.OrdersFilter

@Composable
fun OrdersFilterDialog(
    currentFilter: OrdersFilter,
    onDismiss: () -> Unit,
    onConfirm: (filter: OrdersFilter) -> Unit
) {
    var filter by remember { mutableStateOf(currentFilter) }

    AlertDialog(
        title = {
            Text(
                text = "Настройка фильтра"
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CheckBoxFilterOption(
                    label = "Только свои заказы",
                    selected = filter.onlyMine,
                    onClick = { filter = filter.copy(onlyMine = !filter.onlyMine) }
                )
                CheckBoxFilterOption(
                    label = "Только без пречека",
                    selected = filter.onlyNotBilled,
                    onClick = { filter = filter.copy(onlyNotBilled = !filter.onlyNotBilled) }
                )
            }
        },
        onDismissRequest = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Закрыть",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(filter)
                    onDismiss()
                }
            ) {
                Text(
                    text = "Применить",
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            }
        }
    )

}

@Composable
private fun CheckBoxFilterOption(
    modifier: Modifier = Modifier,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Checkbox(
            checked = selected,
            onCheckedChange = null
        )
        Text(
            text = label,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize
        )
    }
}












