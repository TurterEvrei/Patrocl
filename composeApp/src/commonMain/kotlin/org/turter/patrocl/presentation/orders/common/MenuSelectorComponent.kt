package org.turter.patrocl.presentation.orders.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.source.Category
import org.turter.patrocl.domain.model.source.Dish
import org.turter.patrocl.presentation.components.SimpleTextField

@Composable
fun MenuSelectorComponent(
    modifier: Modifier = Modifier,
    rootCategory: Category,
    allDishes: List<Dish>,
    onDishClick: (Dish) -> Unit
) {
    var currentCategory by remember { mutableStateOf(rootCategory) }
    val backStack by remember { mutableStateOf(ArrayDeque<Category>()) }

    var searchQuary by remember { mutableStateOf("") }
    var filteredDishes by remember { mutableStateOf(allDishes) }
    filteredDishes = if (searchQuary.isEmpty()) {
        allDishes.filter { currentCategory.dishIdList.contains(it.id) }
    } else {
        allDishes.filter { dish -> dish.name.contains(other = searchQuary, ignoreCase = true) }
    }

    Surface(
        modifier = modifier
    ) {
        Column {
            SimpleTextField(
                modifier = Modifier
                    .height(ButtonDefaults.MinHeight)
                    .fillMaxWidth(),
                value = searchQuary,
                singleLine = true,
                onValueChange = { searchQuary = it },
                placeholder = { Text(text = "Блюдо...") },
                leadingIcon = { Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Filter"
                ) },
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
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
                if (searchQuary.isEmpty()) items(
                    items = currentCategory.childList,
                    key = {it.guid}
                ) {category ->
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
                    key = {it.guid}
                ) {dish ->
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onDishClick(dish) },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(text = dish.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

