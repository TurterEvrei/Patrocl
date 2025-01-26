package org.turter.patrocl.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

sealed class DateTimePickerState {
    data object ShowDate : DateTimePickerState()
    data object ShowTime : DateTimePickerState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerModal(
    onSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val instant = Clock.System.now()
    val currentDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= instant.minus(1.toDuration(DurationUnit.DAYS))
                    .toEpochMilliseconds()
                        && utcTimeMillis <= instant.plus(30.toDuration(DurationUnit.DAYS))
                    .toEpochMilliseconds()
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year >= currentDateTime.year
            }
        }
    )

//    var dateMillis by remember { mutableStateOf<Long?>(null) }
    var state by remember { mutableStateOf<DateTimePickerState>(DateTimePickerState.ShowDate) }

    when (state) {
        is DateTimePickerState.ShowDate -> DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    enabled = datePickerState.selectedDateMillis != null,
                    onClick = {
//                        dateMillis = datePickerState.selectedDateMillis
                        state = DateTimePickerState.ShowTime
                    }
                ) {
                    Text("Далее".uppercase())
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Закрыть".uppercase())
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }

        is DateTimePickerState.ShowTime -> AdvancedTimePicker(
            initHour = currentDateTime.hour,
            initMinute = currentDateTime.minute,
            onConfirm = { timeState ->
                datePickerState.selectedDateMillis?.let { long ->
                    onSelected(
                        LocalDateTime(
                            date = Instant.fromEpochMilliseconds(long)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                                .date,
                            time = LocalTime(timeState.hour, timeState.minute)
                        )
                    )
                    onDismiss()
                }
            },
            onBack = { state = DateTimePickerState.ShowDate },
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedTimePicker(
    onConfirm: (TimePickerState) -> Unit,
    initHour: Int = 0,
    initMinute: Int = 0,
    onBack: () -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initHour,
        initialMinute = initMinute,
        is24Hour = true,
    )

    AdvancedTimePickerDialog(
        onDismiss = { onDismiss() },
        onBack = onBack,
        onConfirm = { onConfirm(timePickerState) }
    ) {
        TimeInput(
            state = timePickerState,
        )
    }
}

@Composable
fun AdvancedTimePickerDialog(
    title: String = "Select Time",
    onDismiss: () -> Unit,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onBack) { Text("Назад".uppercase()) }
                    TextButton(onClick = onConfirm) { Text("OK".uppercase()) }
                }
            }
        }
    }
}