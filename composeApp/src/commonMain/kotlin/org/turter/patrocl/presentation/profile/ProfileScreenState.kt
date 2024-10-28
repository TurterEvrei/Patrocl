package org.turter.patrocl.presentation.profile

import org.turter.patrocl.domain.model.person.Employee
import org.turter.patrocl.domain.model.person.Employee.CompanyEmbedded
import org.turter.patrocl.domain.model.person.Waiter

sealed class ProfileScreenState {
    data object Initial: ProfileScreenState()

    data object Loading: ProfileScreenState()

    data class Content(
        val waiter: Waiter,
        val employee: Employee,
        var isChangePreferCompanyDialogOpen: Boolean = false,
        var newPreferCompany: CompanyEmbedded? = null,
        var isChangingCompany: Boolean,
        var isUpdatingMenu: Boolean = false,
        var isUpdatingTables: Boolean = false
    ): ProfileScreenState()

    data class Error(val cause: Throwable): ProfileScreenState()

}