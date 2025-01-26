package org.turter.patrocl.presentation.components.btn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class FABItem(
    val icon: ImageVector,
    val text: String,
    val action: () -> Unit = {}
)

@Composable
fun ExpandableFAB(
    modifier: Modifier = Modifier,
    items: List<FABItem>,
    defaultFabButton: FABItem = FABItem(icon = Icons.Default.MoreVert, text = "Открыть"),
    openedFabButton: FABItem = FABItem(icon = Icons.Default.Close, text = "Закрыть")
) {
    var buttonClicked by remember {
        mutableStateOf(false)
    }

    val rotation by animateFloatAsState(
        targetValue = if (buttonClicked) 180f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    val interactionSource = MutableInteractionSource()

    Card(
        modifier = modifier.widthIn(min = 56.dp, max = 180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        // parent layout
        Column {
            // The Expandable Sheet layout
            AnimatedVisibility(
                visible = buttonClicked,
                enter = expandVertically(tween(300)) + fadeIn(),
                exit = shrinkVertically(tween(200)) + fadeOut(
                    animationSpec = tween(100)
                )
            ) {
                // display the items
                Column(
                    modifier = Modifier
                        .padding(vertical = 15.dp, horizontal = 15.dp)
                ) {
                    items.forEach { item ->
                        Row(modifier = Modifier
                            .padding(vertical = 10.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = {
                                    item.action()
                                    buttonClicked = false
                                }
                            )) {
                            Icon(
                                imageVector = item.icon, contentDescription = "refresh"
                            )

                            Spacer(modifier = Modifier.width(15.dp))

                            Text(text = item.text)
                        }
                    }
                }
            }

            // The FAB main button
            FloatingActionButton(
                onClick = {
                    buttonClicked = !buttonClicked
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.rotate(rotation),
                        imageVector = if (buttonClicked) openedFabButton.icon
                        else defaultFabButton.icon,
                        contentDescription = if (buttonClicked) openedFabButton.text
                        else defaultFabButton.text
                    )
                    AnimatedVisibility(
                        visible = buttonClicked,
                        enter = expandVertically(animationSpec = tween(300)) + fadeIn(),
                        exit = shrinkVertically(tween(200)) + fadeOut(tween(100))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp)
                        ) {
                            Text(text = openedFabButton.text)
                        }
                    }
                }
            }
        }
    }
}