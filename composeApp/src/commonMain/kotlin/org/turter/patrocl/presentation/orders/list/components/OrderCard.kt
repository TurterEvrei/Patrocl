package org.turter.patrocl.presentation.orders.list.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.order.OrderPreview
import org.turter.patrocl.ui.icons.Fact_check

@Composable
fun OrderCard(
    modifier: Modifier = Modifier,
    order: OrderPreview,
    onCardClick: (String) -> Unit
) {
    val cardContainerColor by animateColorAsState(
        targetValue = if (order.bill) MaterialTheme.colorScheme.surfaceContainerLow
//        else CardDefaults.cardColors().containerColor,
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(durationMillis = 500)
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = cardContainerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        onClick = { onCardClick(order.guid) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Стол: ${order.name}",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
                AnimatedVisibility(
                    visible = order.bill,
                    enter = fadeIn(animationSpec = tween(300)) + scaleIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)) + scaleOut(animationSpec = tween(300))
                ) {
                    Icon(
                        imageVector = Fact_check,
                        contentDescription = "Bill icon"
                    )
                }
            }
            Text(
                text = "Официант: \n - ${order.waiterName}",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                maxLines = 2
            )
            Text(
                text = "Создан в: ${order.getFormattedDate()}",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            )
            Text(
                text = "Сумма: ${order.sum}",
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}