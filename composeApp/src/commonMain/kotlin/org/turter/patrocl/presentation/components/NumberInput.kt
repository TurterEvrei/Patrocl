package org.turter.patrocl.presentation.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.flow.map

@Composable
fun FloatNaturalInput(
    modifier: Modifier = Modifier,
    initialValue: Float? = null,
    label: String = "Кол-во",
    validate: (Float) -> Boolean = { true },
    onValueChange: (Float) -> Unit
) {
    var quantity by remember { mutableStateOf(initialValue?.toString() ?: "1") }
    var hasFocus by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = modifier
            .onFocusChanged { focusState ->
                if (!focusState.isFocused && hasFocus && quantity.isEmpty()) {
                    val value = initialValue?.toString() ?: "1"
                    quantity = value
                    onValueChange(value.toFloat())
                }
                hasFocus = focusState.isFocused
            },
        value = quantity,
        onValueChange = {
            quantity = when (val res = it.toFloatOrNull()) {
                null -> if (it.isEmpty()) it else quantity
                else -> if (res > 0 && validate(res)) {
                    onValueChange(res)
                    it
                } else quantity
            }
        },
        label = { Text(label) },
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}

@Composable
fun IntNaturalInput(
    modifier: Modifier = Modifier,
    value: Int,
    label: String = "Кол-во",
    onValueChange: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = modifier,
        value = value.toString(),
        onValueChange = {
            onValueChange(
                when (val res = it.toIntOrNull()) {
                    null -> value
                    else -> if (res > 0) {
                        res
                    } else value
                }
            )
        },
        label = { Text(label) },
        keyboardActions = KeyboardActions(
            onDone = {
                println(keyboardController)
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}
