package org.turter.patrocl.presentation.orders.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.menu.CategoryDetailed
import org.turter.patrocl.domain.model.menu.DishDetailed
import org.turter.patrocl.presentation.components.SearchTextField

@Composable
fun MenuSelectorComponent(
    modifier: Modifier = Modifier,
    rootCategory: CategoryDetailed,
    allDishes: List<DishDetailed>,
    onDishClick: (DishDetailed) -> Unit
) {
    var currentCategory by remember { mutableStateOf(rootCategory) }
    val backStack by remember { mutableStateOf(ArrayDeque<CategoryDetailed>()) }

    var searchQuary by remember { mutableStateOf("") }
    var filteredDishes by remember { mutableStateOf(allDishes) }
    filteredDishes = if (searchQuary.isEmpty()) {
        currentCategory.dishes
    } else {
        allDishes.filter { dish -> dish.name.contains(other = searchQuary, ignoreCase = true) }
    }

    Surface(
        modifier = modifier
            .fillMaxHeight(0.5f)
            .padding(horizontal = 8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("Меню", fontWeight = FontWeight.Medium)
            }
            HorizontalDivider(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .align(Alignment.CenterHorizontally),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            SearchTextField(
                modifier = Modifier
                    .height(ButtonDefaults.MinHeight)
                    .padding(bottom = 4.dp)
                    .fillMaxWidth(),
                value = searchQuary,
                onValueChange = { searchQuary = it },
                placeholder = { Text(text = "Блюдо...") },
                textColor = MaterialTheme.colorScheme.onSurface,
                colors = OutlinedTextFieldDefaults.colors()
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (backStack.isNotEmpty()) item {
                    FilledTonalButton(
                        shape = RoundedCornerShape(4.dp),
                        onClick = { currentCategory = backStack.removeLast() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
                if (searchQuary.isEmpty()) items(
                    items = currentCategory.childList,
                    key = { it.guid }
                ) { category ->
                    FilledTonalButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            backStack.addLast(currentCategory)
                            currentCategory = category
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(text = category.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                items(
                    items = filteredDishes,
                    key = { it.guid }
                ) { dish ->
                    DishComponent(
                        modifier = Modifier.fillMaxWidth(),
                        name = dish.name,
                        type = DishWarningType.of(dish.onStop, dish.remainingCount),
                        onClick = { onDishClick(dish) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DishComponent(
    modifier: Modifier,
    name: String,
    type: DishWarningType,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
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

private sealed class DishWarningType {
    data object None : DishWarningType()
    data object OnStop : DishWarningType()
    data object LowRemain : DishWarningType()

    companion object {
        fun of(onStop: Boolean, remainCount: Int) = if (onStop) OnStop
        else if (remainCount < 5) LowRemain
        else None
    }
}
