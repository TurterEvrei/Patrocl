package org.turter.patrocl.presentation.orders.item.new.edit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.menu.DishDetailed
import org.turter.patrocl.presentation.components.SearchTextField
import org.turter.patrocl.presentation.components.dialog.FullscreenDialog
import org.turter.patrocl.presentation.orders.common.DishWarningType
import org.turter.patrocl.ui.icons.Restaurant_menu

@Composable
fun DishPickerDialog(
    dishes: List<DishDetailed>,
    currentDish: DishDetailed,
    onDismiss: () -> Unit,
    onConfirm: (dish: DishDetailed) -> Unit
) {
    var searchString by remember { mutableStateOf("") }
    var newSelectedDish by remember { mutableStateOf(currentDish) }
    val filteredDishes = dishes
        .filter {
            if (searchString.isNotEmpty()) it.name.contains(searchString, true) else true
        }
        .toList()

    FullscreenDialog(
        icon = { Icon(imageVector = Restaurant_menu, contentDescription = "Menu icon") },
        label = "Выбор блюда",
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                SearchTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    value = searchString,
                    onValueChange = { searchString = it },
                    placeholder = { Text(text = "Название...") },
                    textColor = MaterialTheme.colorScheme.onSurface,
                    colors = OutlinedTextFieldDefaults.colors()
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = filteredDishes, key = { it.id }) { dish ->
                        DishPickerElement(
                            type = DishWarningType.of(dish.onStop, dish.remainingCount),
                            selected = dish.id == newSelectedDish.id,
                            name = dish.name,
                            onClick = { newSelectedDish = dish }
                        )
                    }
                }
            }
        },
        onDismiss = onDismiss,
        onConfirm = {
            onConfirm(newSelectedDish)
            onDismiss()
        }
    )
}

@Composable
private fun DishPickerElement(
    modifier: Modifier = Modifier,
    type: DishWarningType,
    selected: Boolean,
    name: String,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 300)
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 300)
    )

    Button(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            when (type) {
                is DishWarningType.OnStop -> Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Lock icon"
                )

                is DishWarningType.LowRemain -> Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = "Warning icon"
                )

                is DishWarningType.None -> {}
            }
            Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}