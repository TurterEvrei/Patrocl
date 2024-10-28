package org.turter.patrocl.presentation.main.components

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import org.turter.patrocl.presentation.error.ErrorComponent
import org.turter.patrocl.presentation.error.ErrorType

@Composable
fun MainErrorScreen(
    errorType: ErrorType,
    onRetry: () -> Unit
) {
    Surface {
        ErrorComponent(
            error = errorType,
            onRetry = onRetry
        )
    }
}