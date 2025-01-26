package org.turter.patrocl.presentation.stoplist.list.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.stoplist.StopListItem
import org.turter.patrocl.presentation.components.FullscreenLoader
import org.turter.patrocl.presentation.components.dialog.RemoveItemsDialog
import org.turter.patrocl.presentation.stoplist.list.StopListScreenState
import org.turter.patrocl.presentation.stoplist.list.StopListUiEvent
import org.turter.patrocl.presentation.stoplist.list.StopListViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StopListComponent(
    vm: StopListViewModel,
    state: StopListScreenState.Main,
    onCreateItem: () -> Unit,
    onOpenItem: (StopListItem) -> Unit
) {
    val selectedItems = state.getSelectedItems()

    var isRemoveDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            StopListTopAppBar(
                selectedCount = selectedItems.count(),
                onRefresh = { vm.sendEvent(StopListUiEvent.RefreshList) },
                onClose = { vm.sendEvent(StopListUiEvent.UnselectAllItems) },
                onDelete = { isRemoveDialogOpen = true }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateItem) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add icon")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = state.items, key = { it.id }) { item ->
                    StopListItemCard(
                        modifier = Modifier.fillMaxWidth().animateItemPlacement(),
                        item = item,
                        selected = state.selectedItemsIds.contains(item.id),
                        onClick = { onOpenItem(item) },
                        onLongClick = { vm.sendEvent(StopListUiEvent.SelectItem(item.id)) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(88.dp))
                }
            }
        }
    }

    RemoveItemsDialog(
        expanded = isRemoveDialogOpen,
        count = selectedItems.count(),
        onDismiss = { isRemoveDialogOpen = false },
        onConfirm = { vm.sendEvent(StopListUiEvent.RemoveSelectedItems) }
    )

    FullscreenLoader(isShown = state.isRemoving)
}