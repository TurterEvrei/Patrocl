package org.turter.patrocl.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.turter.patrocl.presentation.stoplist.list.StopListScreen

class StopListTab: Tab {
    @Composable
    override fun Content() {
        Navigator(StopListScreen())
    }

    override val options: TabOptions
        @Composable
        get() {
            val painterIcon = rememberVectorPainter(Icons.Default.Lock)
            return remember {
                TabOptions(
                    index = 0u,
                    title = "Stop list",
                    icon = painterIcon
                )
            }
        }
}