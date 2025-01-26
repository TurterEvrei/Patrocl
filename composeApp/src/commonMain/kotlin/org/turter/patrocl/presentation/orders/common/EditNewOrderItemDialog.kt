package org.turter.patrocl.presentation.orders.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.menu.DishDetailed
import org.turter.patrocl.domain.model.menu.DishModifier
import org.turter.patrocl.domain.model.order.NewOrderItem
import org.turter.patrocl.presentation.components.DropDownMenuWithFilter
import org.turter.patrocl.presentation.components.FloatNaturalInput
import org.turter.patrocl.presentation.components.IntNaturalInput
import org.turter.patrocl.presentation.components.dialog.DialogComponentsDivider
import org.turter.patrocl.presentation.components.dialog.FullscreenDialog

@Composable
fun EditNewOrderItemDialog(
    expanded: Boolean,
    orderItem: NewOrderItem?,
    allModifiers: List<DishModifier>,
    allDishes: List<DishDetailed>,
    onDismiss: () -> Unit,
    onConfirm: (orderItem: NewOrderItem) -> Unit
) {
    if (expanded) {
        var selectedDish: DishDetailed? by remember {
            mutableStateOf(allDishes.find { it.id == orderItem?.dishId })
        }
        var quantity by remember { mutableFloatStateOf(orderItem?.quantity ?: 1f) }
        val modifiers = remember {
            mutableStateListOf(*orderItem?.modifiers?.toTypedArray() ?: emptyArray())
        }

        var isCommentCreateDialogOpened by remember { mutableStateOf(false) }

        FullscreenDialog(
            icon = { Icon(imageVector = Icons.Outlined.Info, contentDescription = "Info icon") },
            label = "Редактирование позиции",
            onDismiss = onDismiss,
            onConfirm = {
                if (selectedDish != null && orderItem != null) {
                    orderItem.dishId = selectedDish!!.id
                    orderItem.dishName = selectedDish!!.name
                    orderItem.quantity = quantity
                    orderItem.modifiers = modifiers
                    onConfirm(orderItem)
                    onDismiss()
                }
            }
        ) {
            DishPickerRow(
                dishes = allDishes,
                selectedDish = selectedDish,
                onSelect = { selectedDish = it },
                initQuantity = orderItem?.quantity,
                onQuantityChange = { quantity = it }
            )

            DialogComponentsDivider()

            ModifiersComponent(
                modifier = Modifier.weight(1f),
                allModifiers = allModifiers,
                currentModifiers = modifiers,
                onOpenCreateCommentDialog = { isCommentCreateDialogOpened = true }
            )

//            DialogComponentsDivider()
//
//            CommentsComponent(
//                modifier = Modifier.weight(1f),
//                comments = comments,
//                onOpenCreateCommentDialog = { isCommentCreateDialogOpened = true }
//            )
        }

        if (isCommentCreateDialogOpened) {
            CreateCommentDialog(
                modifiers = modifiers,
                onDismiss = { isCommentCreateDialogOpened = false }
            )
        }
    }
}

@Composable
private fun DishPickerRow(
    dishes: List<DishDetailed>,
    selectedDish: DishDetailed?,
    onSelect: (DishDetailed) -> Unit,
    initQuantity: Float?,
    onQuantityChange: (Float) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DropDownMenuWithFilter(
            modifier = Modifier.weight(2f),
            selectedItem = selectedDish,
            items = dishes,
            limit = 30,
            label = "Блюдо",
            getName = { name },
            onSelect = onSelect
        )

        FloatNaturalInput(
            modifier = Modifier.weight(1f),
            initialValue = initQuantity,
            onValueChange = onQuantityChange
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ColumnScope.ModifiersComponent(
    modifier: Modifier = Modifier,
    allModifiers: List<DishModifier>,
    currentModifiers: MutableList<NewOrderItem.Modifier>,
    onOpenCreateCommentDialog: () -> Unit
) {
    var targetModifier by remember { mutableStateOf<DishModifier?>(null) }
    var targetModifierQuantity by remember { mutableIntStateOf(1) }

    val checkAndAddModifier: () -> Unit = {
        targetModifier?.let { target ->
            if (targetModifierQuantity > 0) {
                when (val mod = currentModifiers.find {
                    it.modifierId == target.id && it.type == NewOrderItem.Modifier.Type.REGULAR
                }) {
                    null -> currentModifiers.add(
                        NewOrderItem.Modifier.regular(
                            modifierId = target.id,
                            name = target.name,
                            quantity = targetModifierQuantity
                        )
                    )

                    else -> {
                        currentModifiers[currentModifiers.indexOf(mod)] = mod.copy(
                            quantity = mod.quantity + targetModifierQuantity
                        )
                    }
                }
                targetModifierQuantity = 1
            }
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.height(28.dp),
            textAlign = TextAlign.Center,
            text = "Модификаторы:"
        )

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DropDownMenuWithFilter(
                modifier = Modifier.weight(3f),
                selectedItem = targetModifier,
                items = allModifiers,
                limit = 30,
                maxHeight = 150.dp,
                label = "Модификатор",
                getName = { name }
            ) {
                targetModifier = it
            }
            IntNaturalInput(
                modifier = Modifier.weight(1f),
                value = targetModifierQuantity,
                label = "К-во"
            ) {
                targetModifierQuantity = it
            }

            OutlinedIconButton(
                modifier = Modifier
                    .height(OutlinedTextFieldDefaults.MinHeight)
                    .weight(1f),
                shape = RoundedCornerShape(4.dp),
                enabled = targetModifier != null && targetModifierQuantity > 0,
                onClick = checkAndAddModifier
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Add"
                )
            }
        }

        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(OutlinedTextFieldDefaults.MinHeight),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.outlinedButtonColors()
                .copy(contentColor = MaterialTheme.colorScheme.onSurface),
            onClick = onOpenCreateCommentDialog
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit icon")
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Написать комментарий",
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (currentModifiers.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .height(50.dp)
                            .animateItemPlacement(),
                    ) {
                        HorizontalDivider()
                        Spacer(Modifier.weight(1f))
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Список пуст",
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.weight(1f))
                        HorizontalDivider()
                    }
                }
            }
            items(
                items = currentModifiers,
                key = { it.modifierId + it.content }
            ) { modifierItem ->
                Column(modifier = Modifier.animateItemPlacement()) {
                    HorizontalDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${modifierItem.name} x${modifierItem.quantity}")
                        IconButton(onClick = {
                            currentModifiers.remove(modifierItem)
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Remove"
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//private fun ColumnScope.CommentsComponent(
//    modifier: Modifier = Modifier,
//    comments: MutableList<String>,
//    onOpenCreateCommentDialog: () -> Unit
//) {
//    Column(
//        modifier = modifier,
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        Row(
//            modifier = Modifier.height(28.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text("Комментарии:")
//            Spacer(modifier = Modifier.weight(1f))
//            IconButton(onClick = onOpenCreateCommentDialog) {
//                Icon(
//                    imageVector = Icons.Default.Add,
//                    contentDescription = "Add icon"
//                )
//            }
//        }
//
//        LazyColumn(
//            modifier = Modifier
//                .weight(1f)
//                .padding(top = 4.dp),
//            verticalArrangement = Arrangement.spacedBy(4.dp)
//        ) {
//            if (comments.isEmpty()) {
//                item {
//                    Column(
//                        modifier = Modifier
//                            .height(50.dp)
//                            .animateItemPlacement()
//                            .clickable(onClick = onOpenCreateCommentDialog),
//                    ) {
//                        HorizontalDivider()
//                        Spacer(Modifier.weight(1f))
//                        Text(
//                            modifier = Modifier.fillMaxWidth(),
//                            text = "Список пуст",
//                            textAlign = TextAlign.Center
//                        )
//                        Spacer(Modifier.weight(1f))
//                        HorizontalDivider()
//                    }
//                }
//            }
//            items(
//                items = comments,
//                key = { it }
//            ) { comment ->
//                Column(modifier = Modifier.animateItemPlacement()) {
//                    HorizontalDivider()
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            modifier = Modifier.weight(9f),
//                            text = comment
//                        )
//                        IconButton(
//                            modifier = Modifier.weight(1f),
//                            onClick = {
//                                comments.remove(comment)
//                            }
//                        ) {
//                            Icon(
//                                Icons.Default.Delete,
//                                contentDescription = "Remove"
//                            )
//                        }
//                    }
//                    HorizontalDivider()
//                }
//            }
//        }
//    }
//}

@Composable
private fun CreateCommentDialog(
    modifiers: MutableList<NewOrderItem.Modifier>,
    onDismiss: () -> Unit
) {
    val comments = modifiers.map { it.content }.filter { it.isNotEmpty() }.toList()
    var newComment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Создание комментария") },
        icon = { Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit icon") },
        text = {
            OutlinedTextField(
                value = newComment,
                onValueChange = { newComment = it },
                label = { Text("Введите комментарий") },
                maxLines = 5
            )
        },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        confirmButton = {
            TextButton(
                enabled = newComment.isNotEmpty() && !comments.contains(newComment),
                onClick = {
                    if (newComment.isNotEmpty() && !comments.contains(newComment)) {
                        modifiers.add(
                            NewOrderItem.Modifier.comment(
                                newComment
                                    .trim()
                                    .replace("\\s+".toRegex(), " ")
                            )
                        )
                        onDismiss()
                        newComment = ""
                    }
                }
            ) {
                Text("Сохранить".uppercase())
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                newComment = ""
            }) {
                Text("Отмена".uppercase())
            }
        },
    )
}