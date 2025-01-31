package org.turter.patrocl.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.turter.patrocl.presentation.auth.LogoutCallback
import org.turter.patrocl.presentation.auth.WelcomeScreen
import org.turter.patrocl.presentation.auth.logout
import org.turter.patrocl.presentation.profile.ProfileScreenState
import org.turter.patrocl.presentation.profile.ProfileUiEvent
import org.turter.patrocl.presentation.profile.ProfileUiEvent.CloseChangePreferCompanyDialog
import org.turter.patrocl.presentation.profile.ProfileUiEvent.ConfirmChangingPreferCompany
import org.turter.patrocl.presentation.profile.ProfileUiEvent.OpenChangePreferCompanyDialog
import org.turter.patrocl.presentation.profile.ProfileUiEvent.SelectNewPreferCompany
import org.turter.patrocl.presentation.profile.ProfileUiEvent.UpdateMenuFromRemote
import org.turter.patrocl.presentation.profile.ProfileUiEvent.UpdateTablesFromRemote
import org.turter.patrocl.presentation.profile.ProfileViewModel

@Composable
fun ProfileContent(
    vm: ProfileViewModel,
    state: ProfileScreenState.Content
) {
    val employee = state.employee
    val waiter = state.waiter
    val navigator = LocalNavigator.currentOrThrow

    val props = ProfileProps.from(
        waiter = waiter,
        employee = employee
    )

    val options = listOf(
        ProfileOption(
            title = "Сменить текущую организацию",
            action = { vm.sendEvent(OpenChangePreferCompanyDialog) }
        ),
        ProfileOption(
            title = "Обновить данные меню",
            isProcess = state.isUpdatingMenu,
            action = { vm.sendEvent(UpdateMenuFromRemote) }
        ),
        ProfileOption(
            title = "Обновить данные столов",
            isProcess = state.isUpdatingTables,
            action = { vm.sendEvent(UpdateTablesFromRemote) }
        ),
        ProfileOption(
            title = "Выйти",
//            action = { vm.sendEvent(ProfileUiEvent.Logout { navigator.replaceAll(WelcomeScreen()) }) }
            action = { navigator.logout() }
        )
    )

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ProfileTopAppBar(
                name = employee.name,
                positionTitle = employee.position.title,
//                onLogout = { vm.sendEvent(ProfileUiEvent.Logout { navigator.replaceAll(WelcomeScreen()) }) }
                onLogout = { navigator.logout() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//
//            ProfileTitle(employee = employee)

            ProfileGroup(label = "Личная информация") {
                props.map { prop ->
                    ProfileProperty(label = prop.name, value = prop.value)
                }
            }

            ProfileGroup(label = "Возможности") {
                options.map { option ->
                    ProfileOptionComponent(
                        label = option.title,
                        isProcess = option.isProcess,
                        onClick = option.action
                    )
                }
            }

        }
    }

    ChangePreferCompanyDialog(
        currentCompany = employee.companyList.find { it.id == employee.preferredCompanyId },
        companies = employee.companyList,
        isExpanded = state.isChangePreferCompanyDialogOpen,
        isProcess = state.isChangingCompany,
        onDismiss = { vm.sendEvent(CloseChangePreferCompanyDialog) },
        onSelect = { vm.sendEvent(SelectNewPreferCompany(it)) },
        onConfirmChanging = { vm.sendEvent(ConfirmChangingPreferCompany) }
    )

}