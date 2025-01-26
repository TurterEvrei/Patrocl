package org.turter.patrocl.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissComponent(
    modifier: Modifier = Modifier,
    onStartToEnd: () -> Unit,
    onEndToStart: () -> Unit,
    bg: @Composable (dismissState: SwipeToDismissBoxState) -> Unit = {},
    content: @Composable RowScope.() -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            when(it) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onStartToEnd()
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onEndToStart()
                }
                SwipeToDismissBoxValue.Settled -> {
                    return@rememberSwipeToDismissBoxState false
                }
            }
            return@rememberSwipeToDismissBoxState true
        },
        positionalThreshold = { it * .25f }
    )
    
    SwipeToDismissBox(
        modifier = modifier
            .fillMaxWidth(),
        state = dismissState,
        backgroundContent = { bg(dismissState) }
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DismissBackground(
    dismissState: SwipeToDismissBoxState,
    startToEndColor: Color = Color.Transparent,
    endToStartColor: Color = Color.Transparent,
    settledColor: Color = Color.Transparent,

) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> startToEndColor
        SwipeToDismissBoxValue.EndToStart -> endToStartColor
        SwipeToDismissBoxValue.Settled -> settledColor
    }

    Row(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxSize()
            .background(color),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            modifier = Modifier.padding(16.dp, 0.dp),
            imageVector = Icons.Default.Delete,
            contentDescription = "delete"
        )
        Spacer(modifier = Modifier)
        Icon(
            modifier = Modifier.padding(16.dp, 0.dp),
            imageVector = Icons.Default.Delete,
            contentDescription = null
        )
    }
}
