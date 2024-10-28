package org.turter.patrocl.presentation.profile.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.turter.patrocl.domain.model.person.Employee
import org.turter.patrocl.domain.model.person.Employee.CompanyEmbedded
import org.turter.patrocl.presentation.components.DropDownMenu
import org.turter.patrocl.presentation.components.LoadingButton

@Composable
fun ChangePreferCompanyDialog(
    currentCompany: CompanyEmbedded?,
    companies: List<CompanyEmbedded>,
    isExpanded: Boolean,
    isProcess: Boolean,
    onDismiss: () -> Unit,
    onSelect: (target: CompanyEmbedded) -> Unit,
    onConfirmChanging: () -> Unit
) {
    if (isExpanded) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Сменить текущую организацию") },
            text = {
                DropDownMenu(
                    label = "Организация",
                    items = companies,
                    selectedItem = currentCompany,
                    getName = { title },
                    onSelect = onSelect
                )
            },
            confirmButton = {
                LoadingButton(
                    onClick = onConfirmChanging,
                    label = "Сохранить",
                    isProcess = isProcess
                )
            },
            dismissButton = {
                Button(
                    shape = RoundedCornerShape(4.dp),
                    onClick = onDismiss
                ) {
                    Text(text = "Закрыть")
                }
            }
        )
    }
}
