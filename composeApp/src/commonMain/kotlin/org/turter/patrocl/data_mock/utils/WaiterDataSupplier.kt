package org.turter.patrocl.data_mock.utils

import org.turter.patrocl.domain.model.person.Waiter

object WaiterDataSupplier {

    fun getWaiter() = Waiter(
        employeeId = "employee-id",
        rkId = "waiter-id-1",
        code = "99",
        name = "Бобби"
    )

}