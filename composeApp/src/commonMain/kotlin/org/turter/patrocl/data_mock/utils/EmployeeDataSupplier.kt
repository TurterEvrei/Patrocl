package org.turter.patrocl.data_mock.utils

import org.turter.patrocl.domain.model.enums.Specialization
import org.turter.patrocl.domain.model.person.Employee

object EmployeeDataSupplier {

    fun getEmployee() = Employee(
        id = "employee-id",
        name = "Biber",
        lastName = "Pupa",
        patronymic = "Petrovich",
        simpleName = "Biba",
        active = true,
        position = Employee.PositionEmbedded(
            id = "position-id",
            title = "Waiter",
            specialization = Specialization.Waiter,
            rankWeight = 0
        ),
        userId = "user-id",
        preferredCompanyId = "company-id-1",
        companyList = listOf(
            Employee.CompanyEmbedded(id = "company-id-1", title = "Company1"),
            Employee.CompanyEmbedded(id = "company-id-2", title = "Company2")
        )
    )

}