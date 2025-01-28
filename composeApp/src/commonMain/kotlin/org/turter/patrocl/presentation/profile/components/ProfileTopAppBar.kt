package org.turter.patrocl.presentation.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
fun ProfileTopAppBar(
    name: String,
    positionTitle: String,
    onLogout: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = name,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = positionTitle,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                )
            }
        },
        actions = {
            IconButton(
                onClick = onLogout
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Menu icon"
                )
            }
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
        )
    )
}