package org.turter.patrocl.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun LoadingButton(
    modifier: Modifier = Modifier,
    label: String,
    isProcess: Boolean,
    onClick: () -> Unit
) {
    val contentAlpha by animateFloatAsState(targetValue = if (isProcess) 0f else 1f)
    val loaderAlpha by animateFloatAsState(targetValue = if (isProcess) 1f else 0f)

    Button(
        onClick = onClick,
        enabled = !isProcess,
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.graphicsLayer { alpha = contentAlpha },
                text = label
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer { alpha = loaderAlpha },
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
fun LoadingTextButton(
    modifier: Modifier = Modifier,
    label: String,
    isProcess: Boolean,
    onClick: () -> Unit
) {
    val loaderAlpha by animateFloatAsState(targetValue = if (isProcess) 1f else 0f)

    TextButton(
        modifier = modifier,
        shape = RectangleShape,
        enabled = !isProcess,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
            CircularProgressIndicator(
                modifier = Modifier
                    .size(20.dp)
                    .graphicsLayer { alpha = loaderAlpha },
                strokeWidth = 2.dp
            )
        }
    }
}