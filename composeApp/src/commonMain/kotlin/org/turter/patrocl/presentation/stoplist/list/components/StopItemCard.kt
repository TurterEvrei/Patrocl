package org.turter.patrocl.presentation.stoplist.list.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.utils.isSoon

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StopListItemCard(
    modifier: Modifier = Modifier,
    selected: Boolean = true,
    item: StopListItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val status = if (item.onStop || item.remainingCount < 1) "закончилось"
    else "осталось ${item.remainingCount} шт."

    val until = item.until.let { if (it.isSoon()) it.formattedUntil() else "-" }

    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        animationSpec = tween(durationMillis = 300)
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurface,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        modifier = modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = item.dishName,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = "Статус: $status",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                maxLines = 1
            )
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = "До: $until",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            )
        }
    }
}

private fun LocalDateTime.formattedUntil() = "дата - $dayOfMonth.$monthNumber.$year, время - $hour:$minute"