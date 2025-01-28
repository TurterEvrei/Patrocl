package org.turter.patrocl.presentation.stoplist.edit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.turter.patrocl.presentation.components.DateTimePickerModal
import org.turter.patrocl.presentation.components.FullscreenLoader
import org.turter.patrocl.presentation.components.SurfaceCard
import org.turter.patrocl.presentation.components.dialog.RemoveItemDialog
import org.turter.patrocl.utils.isSoon
import org.turter.patrocl.utils.toFormatString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditStopListItemComponent(
    vm: EditStopListItemViewModel,
    state: EditStopListItemScreenState.Main
) {
    val navigator = LocalNavigator.currentOrThrow

    var isDateTimePickerOpened by remember { mutableStateOf(false) }
    var isConfirmRemoveDialogOpened by remember { mutableStateOf(false) }
    val originUntil = state.originalItem.until

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back icon"
                        )
                    }
                },
                title = {
                    Text(
                        text = "Редактирование",
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = { isConfirmRemoveDialogOpened = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete icon"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            )
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
                SurfaceCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    label = "Позиция",
                    actionLabel = "Сохранить",
                    action = { vm.sendEvent(EditStopListItemUiEvent.Save { navigator.pop() } ) },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.6f)
                            .padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Блюдо:",
                                fontWeight = FontWeight.Bold,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                text = state.originalItem.dishName,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Осталось:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    text = "${state.newRemainCount} шт.",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Slider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                value = state.newRemainCount.toFloat(),
                                onValueChange = {
                                    vm.sendEvent(
                                        EditStopListItemUiEvent.SetRemainCount(it.toInt())
                                    )
                                },
                                steps = 4,
                                valueRange = 0f..5f
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Актуально до:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.weight(1f))
                                AnimatedContent(
                                    targetState = originUntil == state.newUntil,
                                    transitionSpec = {
                                        fadeIn(initialAlpha = 0.4f) togetherWith
                                                fadeOut(targetAlpha = 0.4f) using
                                                SizeTransform(clip = false)
                                    }
                                ) {
                                    if (it) {
                                        TextButton(onClick = {
                                            vm.sendEvent(
                                                EditStopListItemUiEvent.SetUntil(null)
                                            )
                                        }) {
                                            Text(text = "Убрать")
                                        }
                                    } else {
                                        TextButton(onClick = {
                                            vm.sendEvent(
                                                EditStopListItemUiEvent.SetUntil(originUntil)
                                            )
                                        }) {
                                            Text(text = "Сбросить")
                                        }
                                    }
                                }
                                Spacer(Modifier.width(4.dp))
                                TextButton(onClick = { isDateTimePickerOpened = true }) {
                                    Text(text = "Выбрать")
                                }
                            }
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                text = state.newUntil?.takeIf { it.isSoon() }?.toFormatString()
                                    ?: "Бессрочно",
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }

    if (isConfirmRemoveDialogOpened) RemoveItemDialog(
        expanded = isConfirmRemoveDialogOpened,
        title = "Удалить данный элемент",
        onDismiss = { isConfirmRemoveDialogOpened = false },
        onConfirm = { vm.sendEvent(EditStopListItemUiEvent.Delete { navigator.pop() } ) }
    )

    if (isDateTimePickerOpened) DateTimePickerModal(
        onDismiss = { isDateTimePickerOpened = false },
        onSelected = { vm.sendEvent(EditStopListItemUiEvent.SetUntil(it)) }
    )

    FullscreenLoader(isShown = state.isSaving)
}