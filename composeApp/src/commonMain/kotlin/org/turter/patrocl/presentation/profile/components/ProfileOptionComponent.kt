package org.turter.patrocl.presentation.profile.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.turter.patrocl.presentation.components.LoadingTextButton

@Composable
fun ProfileOptionComponent(
    label: String,
    isProcess: Boolean,
    onClick: () -> Unit
) {
    LoadingTextButton(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp),
        label = label,
        isProcess = isProcess,
        onClick = onClick
    )
}