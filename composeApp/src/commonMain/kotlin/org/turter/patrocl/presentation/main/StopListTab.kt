package org.turter.patrocl.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.turter.patrocl.presentation.stoplist.list.StopListScreen

object StopListTab : Tab {
    @Composable
    override fun Content() {
        Navigator(
            screen = StopListScreen(),
            disposeBehavior = NavigatorDisposeBehavior(
                disposeNestedNavigators = false,
                disposeSteps = false
            )
        )
    }

    override val options: TabOptions
        @Composable
        get() {
            val painterIcon = rememberVectorPainter(Icons.Default.Warning)
            return remember {
                TabOptions(
                    index = 0u,
                    title = "Стоп-лист",
                    icon = painterIcon
                )
            }
        }
}