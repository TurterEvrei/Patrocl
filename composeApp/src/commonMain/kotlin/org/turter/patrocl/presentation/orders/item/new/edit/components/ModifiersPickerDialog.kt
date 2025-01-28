package org.turter.patrocl.presentation.orders.item.new.edit.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
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
import org.turter.patrocl.domain.model.menu.DishModifier
import org.turter.patrocl.domain.model.menu.ModifiersGroupDetailed
import org.turter.patrocl.presentation.components.SearchTextField
import org.turter.patrocl.presentation.components.dialog.FullscreenDialog

@Composable
fun ModifierPickerDialog(
    modifier: Modifier = Modifier,
    rootGroup: ModifiersGroupDetailed,
    allModifiers: List<DishModifier>,
    onDismiss: () -> Unit,
    onConfirm: (DishModifier) -> Unit
) {
    var currentGroup by remember { mutableStateOf(rootGroup) }
    val backStack by remember { mutableStateOf(ArrayDeque<ModifiersGroupDetailed>()) }

    var searchQuery by remember { mutableStateOf("") }
    var filteredModifiers by remember { mutableStateOf(allModifiers) }
    filteredModifiers = if (searchQuery.isEmpty()) {
        currentGroup.modifiers
    } else {
        allModifiers.filter { mod -> mod.name.contains(other = searchQuery, ignoreCase = true) }
    }

    var selectedModifier by remember { mutableStateOf<DishModifier?>(null) }

    FullscreenDialog(
        icon = {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Modifiers icon"
            )
        },
        label = "Выбор модификатора",
        content = {
            Column(modifier = Modifier.fillMaxSize()) {
                SearchTextField(
                    modifier = Modifier
                        .height(ButtonDefaults.MinHeight)
                        .padding(bottom = 4.dp)
                        .fillMaxWidth(),
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(text = "Модификатор...") },
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
                            onClick = { currentGroup = backStack.removeLast() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                    if (searchQuery.isEmpty()) items(
                        items = currentGroup.childList,
                        key = { it.guid }
                    ) { modifiersGroup ->
                        FilledTonalButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                backStack.addLast(currentGroup)
                                currentGroup = modifiersGroup
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = modifiersGroup.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    items(
                        items = filteredModifiers,
                        key = { it.guid }
                    ) { mod ->
                        ModifierPickerElement(
                            modifier = Modifier.fillMaxWidth(),
                            selected = mod == selectedModifier,
                            name = mod.name,
                            onClick = { selectedModifier = mod }
                        )
                    }
                }
            }
        },
        onDismiss = onDismiss,
        confirmEnabled = selectedModifier != null,
        onConfirm = {
            selectedModifier?.let {
                onConfirm(it)
                onDismiss()
            }
        }
    )


}

@Composable
private fun ModifierPickerElement(
    modifier: Modifier = Modifier,
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
        Text(text = name, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}