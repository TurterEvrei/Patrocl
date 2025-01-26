package org.turter.patrocl.presentation.orders.common

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.presentation.components.DropDownMenuWithFilter
import org.turter.patrocl.presentation.components.SearchTextField
import org.turter.patrocl.presentation.components.SimpleTextField
import org.turter.patrocl.presentation.components.dialog.FullscreenDialog
import org.turter.patrocl.ui.icons.Table_restaurant

@Composable
fun SelectTableComponent(
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    tables: List<Table>,
    selectedTable: Table? = null,
    enabled: Boolean = true,
    onSelectTable: (Table) -> Unit
) {
    var isTablePickerOpened by remember { mutableStateOf(false) }

    FilledTonalButton(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        enabled = enabled,
        colors = colors,
        onClick = { isTablePickerOpened = true }
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Стол: ${selectedTable?.name ?: "не выбран"}"
        )
    }

    TablePickerDialog(
        isOpened = isTablePickerOpened,
        tables = tables,
        selectedTable = selectedTable,
        onDismiss = { isTablePickerOpened = false },
        onSelectTable = onSelectTable
    )
}

@Composable
fun TablePickerDialog(
    isOpened: Boolean,
    tables: List<Table>,
    selectedTable: Table?,
    onDismiss: () -> Unit,
    onSelectTable: (Table) -> Unit
) {
    var searchString by remember { mutableStateOf("") }
    var newSelectedTable by remember { mutableStateOf(selectedTable) }
    val filteredTables = tables
        .filter { if (searchString.isNotEmpty()) it.name.contains(searchString) else true }
        .toList()

    if (isOpened) {
        FullscreenDialog(
            icon = { Icon(imageVector = Table_restaurant, contentDescription = "Table icon") },
            label = "Выбор стола",
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

                    LazyVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 4.dp, vertical = 4.dp),
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items = filteredTables, key = { it.guid }) { table ->
                            TablePickerElement(
                                isSelect = table.guid == newSelectedTable?.guid,
                                tableName = table.name,
                                onClick = { newSelectedTable = table }
                            )
                        }
                    }
                }
            },
            onDismiss = onDismiss,
            confirmEnabled = newSelectedTable != null,
            onConfirm = {
                newSelectedTable?.let {
                    onSelectTable(it)
                    onDismiss()
                }
            }
        )
    }
}

@Composable
private fun TablePickerElement(
    modifier: Modifier = Modifier,
    isSelect: Boolean,
    tableName: String,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelect) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 300)
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelect) MaterialTheme.colorScheme.onPrimaryContainer
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
        Text(text = tableName, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}