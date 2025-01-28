package org.turter.patrocl.presentation.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun FullscreenDialog(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: String,
    confirmLabel: String = "Сохранить",
    dismissLabel: String = "Отменить",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmEnabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            CloseDialogButton(onClick = onDismiss)

            Column(modifier = Modifier.fillMaxSize()) {
                DialogHeader(icon = icon, label = label)

                DialogComponentsDivider()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    content()
                }

                DialogFooter(
                    confirmLabel = confirmLabel,
                    dismissLabel = dismissLabel,
                    confirmEnabled = confirmEnabled,
                    onDismiss = onDismiss,
                    onConfirm = onConfirm
                )
            }
        }
    }
}

@Composable
fun DialogComponentsDivider(thickness: Dp = 2.dp) {
    HorizontalDivider(
        thickness = 2.dp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun BoxScope.CloseDialogButton(
    size: Dp = 84.dp,
    padding: Dp = 16.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .padding(padding)
            .align(Alignment.TopEnd)
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.Center),
            onClick = onClick
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close icon")
        }
    }
}

@Composable
private fun DialogHeader(
    height: Dp = 84.dp,
    padding: Dp = 16.dp,
    icon: @Composable () -> Unit,
    label: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        icon()
        Text(
            text = label,
            fontSize = MaterialTheme.typography.titleMedium.fontSize
        )
    }
}

@Composable
private fun DialogFooter(
    modifier: Modifier = Modifier,
    confirmLabel: String,
    dismissLabel: String,
    confirmEnabled: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.primary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            TextButton(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                shape = RectangleShape,
                onClick = onDismiss
            ) {
                Text(dismissLabel)
            }
            Button(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                enabled = confirmEnabled,
                shape = RectangleShape,
                onClick = onConfirm
            ) {
                Text(confirmLabel)
            }
        }
    }
}