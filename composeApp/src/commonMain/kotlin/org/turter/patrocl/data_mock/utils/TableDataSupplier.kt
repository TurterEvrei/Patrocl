package org.turter.patrocl.data_mock.utils

import org.turter.patrocl.domain.model.source.Table

object TableDataSupplier {

    fun getTables() = listOf(
        Table(
            id = "table-id-1",
            guid = "table-guid-1",
            code = "table-code-1",
            name = "33",
            status = "ok",
            hall = "hall-id-root"
        ),
        Table(
            id = "table-id-2",
            guid = "table-guid-2",
            code = "table-code-2",
            name = "34",
            status = "ok",
            hall = "hall-id-root"
        ),
        Table(
            id = "table-id-3",
            guid = "table-guid-3",
            code = "table-code-3",
            name = "54",
            status = "ok",
            hall = "hall-id-root"
        ),
        Table(
            id = "table-id-4",
            guid = "table-guid-4",
            code = "table-code-4",
            name = "21",
            status = "ok",
            hall = "hall-id-root"
        )
    )

}