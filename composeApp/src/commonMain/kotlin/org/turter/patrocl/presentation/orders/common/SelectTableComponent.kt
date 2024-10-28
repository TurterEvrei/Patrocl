package org.turter.patrocl.presentation.orders.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.turter.patrocl.domain.model.source.Table
import org.turter.patrocl.presentation.components.DropDownMenuWithFilter

@Composable
fun SelectTableComponent(
    modifier: Modifier = Modifier,
    tables: List<Table>,
    selectedTable: Table? = null,
    enabled: Boolean = true,
    onSelectTable: (Table) -> Unit
) {
    DropDownMenuWithFilter(
        modifier = modifier,
        label = "Стол",
        items = tables,
        selectedItem = selectedTable,
        enabled = enabled,
        getName = { name },
        onSelect = onSelectTable
    )

}