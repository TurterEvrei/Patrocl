package org.turter.patrocl

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.turter.patrocl.presentation.main.MainScreen
import org.turter.patrocl.ui.theme.AppTheme

@Composable
fun App(
) {
    AppTheme {
        Navigator(MainScreen())
    }
}