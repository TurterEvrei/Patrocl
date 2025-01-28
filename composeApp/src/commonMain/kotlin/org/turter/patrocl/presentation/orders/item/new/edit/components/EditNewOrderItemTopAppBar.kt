package org.turter.patrocl.presentation.orders.item.new.edit.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNewOrderItemTopAppBar(
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back icon"
                )
            }
        },
        title = {
            Text(
                text = "Редактирование позиции",
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.SemiBold
            )
        },
        actions = {
            IconButton(
                onClick = onDelete
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete icon")
            }
        },
        colors = topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    )
}