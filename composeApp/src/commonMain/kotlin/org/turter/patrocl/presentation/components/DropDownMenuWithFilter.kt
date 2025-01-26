package org.turter.patrocl.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropDownMenuWithFilter(
    modifier: Modifier = Modifier,
    label: String,
    items: List<T>,
    limit: Int = -1,
    selectedItem: T? = null,
    enabled: Boolean = true,
    maxHeight: Dp = 250.dp,
    getName: T.() -> String,
    onSelect: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(selectedItem?.getName() ?: "") }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val filteredItems = if (searchQuery.isEmpty()) {
        if (limit > 0) items.take(limit) else items
    } else {
        if (limit > 0) items
            .filter { it.getName().contains(searchQuery, ignoreCase = true) }
            .take(limit)
        else items
            .filter { it.getName().contains(searchQuery, ignoreCase = true) }
    }

    val onSelectItem = { item: T ->
        searchQuery = item.getName()
        onSelect(item)
        expanded = false
        focusManager.clearFocus()
        keyboardController?.hide()
    }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = modifier
                .menuAnchor()
                .clickable { if (enabled) expanded = true },
            label = { Text(label) },
            singleLine = true,
            enabled = enabled,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null
                )
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    if (filteredItems.isNotEmpty()) filteredItems.first()?.let(onSelectItem)
                }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            )
        )

        ExposedDropdownMenu(
            modifier = Modifier
                .exposedDropdownSize()
                .heightIn(min = 20.dp, max = maxHeight),
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                searchQuery = selectedItem?.getName() ?: ""
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        ) {

            filteredItems.forEach { item ->
                DropdownMenuItem(
                    onClick = { onSelectItem(item) },
                    text = { Text(text = item.getName()) }
                )
            }
        }
    }
}