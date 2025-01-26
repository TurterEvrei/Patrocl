package org.turter.patrocl.presentation.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.turter.patrocl.presentation.profile.ProfileScreen

data class ProfileTab(
    val logout: () -> Unit
): Tab {
    @Composable
    override fun Content() {
        Navigator(ProfileScreen(logout = logout))
    }

    override val options: TabOptions
        @Composable
        get() {
            val iconPainter = rememberVectorPainter(Icons.Default.Person)
            return remember {
                TabOptions(
                    index = 2u,
                    title = "Profile",
                    icon = iconPainter
                )
            }
        }
}