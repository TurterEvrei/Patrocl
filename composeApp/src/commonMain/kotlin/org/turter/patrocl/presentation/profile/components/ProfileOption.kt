package org.turter.patrocl.presentation.profile.components

data class ProfileOption(
    val title: String,
    val isProcess: Boolean = false,
    val action: () -> Unit
) {
}