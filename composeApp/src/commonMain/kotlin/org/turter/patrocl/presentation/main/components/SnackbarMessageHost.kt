package org.turter.patrocl.presentation.main.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.Message
import org.turter.patrocl.presentation.error.ErrorType

@Composable
fun SnackbarMessageHost(
    paddingValues: PaddingValues = PaddingValues(),
//    messageState: State<Message<String>>
    message: Message<String>
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

//    val message by messageState

    val snackBarMessage = when (message) {
        is Message.Initial -> null
        is Message.Success -> SnackbarMessageData(
            content = message.content,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
        is Message.Error -> {
            val errorType = ErrorType.from(message.exception)
            SnackbarMessageData(
                content = errorType.getMessage(),
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        }
        is Message.Content -> SnackbarMessageData(
            content = message.content,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }

    LaunchedEffect(message) {
        scope.launch {
            println("NEW MESSAGE IN LAUNCH EFFECT: $message")
            snackBarMessage?.let {
                snackbarHostState.showSnackbar(
                    message = it.content,
//                    actionLabel = "Dismiss",
                    withDismissAction = true,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    SnackbarHost(
        modifier = Modifier
            .fillMaxHeight()
            .padding(paddingValues),
        hostState = snackbarHostState,
        snackbar = { snackbarData ->
            snackBarMessage?.let {
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = it.containerColor,
                    contentColor = it.contentColor,
                    actionColor = it.contentColor
                )
            }
        }
    )
}

class SnackbarMessageData(
    val content: String,
    val containerColor: Color,
    val contentColor: Color
)