package org.turter.patrocl.presentation.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.turter.patrocl.domain.model.DataStatus
import org.turter.patrocl.domain.model.FetchState.Finished
import org.turter.patrocl.domain.model.person.Employee.CompanyEmbedded
import org.turter.patrocl.domain.service.EmployeeService
import org.turter.patrocl.domain.service.MenuService
import org.turter.patrocl.domain.service.TableService
import org.turter.patrocl.domain.service.WaiterService
import org.turter.patrocl.presentation.profile.ProfileUiEvent.CloseChangePreferCompanyDialog
import org.turter.patrocl.presentation.profile.ProfileUiEvent.ConfirmChangingPreferCompany
import org.turter.patrocl.presentation.profile.ProfileUiEvent.OpenChangePreferCompanyDialog
import org.turter.patrocl.presentation.profile.ProfileUiEvent.RefreshProfileInfoFromRemote
import org.turter.patrocl.presentation.profile.ProfileUiEvent.SelectNewPreferCompany
import org.turter.patrocl.presentation.profile.ProfileUiEvent.UpdateMenuFromRemote
import org.turter.patrocl.presentation.profile.ProfileUiEvent.UpdateTablesFromRemote

sealed class ProfileUiEvent {
    data object OpenChangePreferCompanyDialog: ProfileUiEvent()
    data object CloseChangePreferCompanyDialog: ProfileUiEvent()
    data class SelectNewPreferCompany(val company: CompanyEmbedded): ProfileUiEvent()
    data object ConfirmChangingPreferCompany: ProfileUiEvent()
    data object UpdateMenuFromRemote: ProfileUiEvent()
    data object UpdateTablesFromRemote: ProfileUiEvent()
    data object RefreshProfileInfoFromRemote: ProfileUiEvent()
}

class ProfileViewModel(
    private val waiterService: WaiterService,
    private val employeeService: EmployeeService,
    private val menuService: MenuService,
    private val tableService: TableService
): ScreenModel {
    private val log = Logger.withTag("ProfileViewModel")

    private val coroutineScope = screenModelScope

    private val _screenState = MutableStateFlow<ProfileScreenState>(ProfileScreenState.Initial)

    val screenState = _screenState.asStateFlow()

    init {
        coroutineScope.launch {
            combine(
                waiterService.getOwnWaiterStateFlow(),
                employeeService.getOwnEmployeeStateFlow(),
                menuService.getMenuDataStatusStateFlow(),
                tableService.getTablesDataStatusStateFlow()
            ) { waiter, employee, menuDataStatus, tablesDataStatus ->
                log.d { "Waiter: $waiter" }
                log.d { "Employee: $employee" }
                log.d { "MenuDataStatus: $menuDataStatus" }
                log.d { "TablesDataStatus: $tablesDataStatus" }
                try {
                    if (waiter is Finished && employee is Finished) {
                        ProfileScreenState.Content(
                            waiter = waiter.result.getOrThrow(),
                            employee = employee.result.getOrThrow(),
                            isChangingCompany = false,
                            isUpdatingMenu = menuDataStatus is DataStatus.Loading,
                            isUpdatingTables = tablesDataStatus is DataStatus.Loading
                        )
                    } else {
                        ProfileScreenState.Loading
                    }
                } catch (e: Exception) {
                    log.d { "Error in init collecting: $e" }
                    e.printStackTrace()
                    ProfileScreenState.Error(e)
                }
            }.collect { newState ->
                _screenState.value = newState
            }
        }
    }

    fun sendEvent(event: ProfileUiEvent) {
        when (event) {
            is OpenChangePreferCompanyDialog -> openChangePreferCompanyDialog()
            is CloseChangePreferCompanyDialog -> closeChangePreferCompanyDialog()
            is SelectNewPreferCompany -> selectNewPreferCompany(target = event.company)
            is ConfirmChangingPreferCompany -> confirmChangingPreferCompany()
            is UpdateMenuFromRemote -> updateMenuFromRemote()
            is UpdateTablesFromRemote -> updateTablesFromRemote()
            is RefreshProfileInfoFromRemote -> refreshProfileInfoFromRemote()
        }
    }

    private fun openChangePreferCompanyDialog() = execInContent {
        isChangePreferCompanyDialogOpen = true
    }

    private fun closeChangePreferCompanyDialog() = execInContent {
        isChangePreferCompanyDialogOpen = false
    }

    private fun selectNewPreferCompany(target: CompanyEmbedded) = execInContent {
        newPreferCompany = target
    }

    private fun confirmChangingPreferCompany() = execInContent {
        val targetId = newPreferCompany?.id
        if (targetId != null && targetId != employee.preferredCompanyId) {
            isChangingCompany = true
            coroutineScope.launch {
                employeeService.changePreferCompany(targetId)
                    .apply {
                        closeChangePreferCompanyDialog()
                        isChangingCompany = false
                    }
            }
        }
    }

    private fun updateMenuFromRemote() = execInContent {
        coroutineScope.launch {
            menuService.refreshMenuFromApi()
        }
    }

    private fun updateTablesFromRemote() = coroutineScope.launch {
        tableService.refreshTablesFromApi()
    }

    private fun refreshProfileInfoFromRemote() = coroutineScope.launch {
        _screenState.value = ProfileScreenState.Loading
        waiterService.updateWaiterFromRemote()
        employeeService.updateEmployeeFromRemote()
    }

    private fun execInContent(action: ProfileScreenState.Content.() -> Unit) {
        val currentState = screenState.value
        if (currentState is ProfileScreenState.Content) {
            _screenState.value = currentState.copy().apply(action)
        }
    }

}