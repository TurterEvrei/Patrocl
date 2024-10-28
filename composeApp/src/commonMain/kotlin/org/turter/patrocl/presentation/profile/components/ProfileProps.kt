package org.turter.patrocl.presentation.profile.components

import org.turter.patrocl.domain.model.person.Employee
import org.turter.patrocl.domain.model.person.Waiter

class ProfileProps {
    companion object {
        fun from(waiter: Waiter, employee: Employee): List<ProfileProp> = listOf(
            ProfileProp("Имя", employee.name),
            ProfileProp("Фамилия", employee.lastName),
            ProfileProp("Отчество", employee.patronymic),
            ProfileProp(
                "Текущая организация",
                employee.companyList.find { it.id == waiter.preferCompanyId }?.title
                    ?: "Нет"
            ),
            ProfileProp("Код официанта", waiter.code)
        )
    }

    data class ProfileProp(val name: String, val value: String)
}
