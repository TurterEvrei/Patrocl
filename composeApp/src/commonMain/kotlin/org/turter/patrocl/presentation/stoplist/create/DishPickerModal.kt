package org.turter.patrocl.presentation.stoplist.create

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.menu.Dish
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.presentation.components.SearchTextField
import org.turter.patrocl.presentation.components.dialog.FullscreenDialog
import org.turter.patrocl.ui.icons.Restaurant_menu

@Composable
fun DishPickerModal(
    dishes: List<Dish>,
    stopList: List<StopListItem>,
    selectedDishId: String,
    onDismiss: () -> Unit,
    onSelectDish: (id: String) -> Unit
) {
    var searchString by remember { mutableStateOf("") }
    var newSelectedDishId by remember { mutableStateOf(selectedDishId) }
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
                                selected = dish.id == newSelectedDishId,
                                enabled = stopList.none { it.dishId == dish.id },
                                name = dish.name,
                                onClick = { newSelectedDishId = dish.id }
                            )
                        }
                    }
                }
            },
            onDismiss = onDismiss,
            confirmEnabled = dishes.any { it.id == newSelectedDishId },
            onConfirm = {
                newSelectedDishId.let {
                    onSelectDish(it)
                    onDismiss()
                }
            }
        )
}

@Composable
private fun DishPickerElement(
    modifier: Modifier = Modifier,
    selected: Boolean,
    enabled: Boolean,
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
        enabled = enabled,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}