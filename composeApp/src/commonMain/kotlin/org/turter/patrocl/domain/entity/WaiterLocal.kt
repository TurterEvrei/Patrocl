package org.turter.patrocl.domain.entity

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class WaiterLocal: RealmObject {
    @PrimaryKey
    var employeeId: String = ""
    var rkId: String = ""
    var code: String = ""
    var name: String = ""
    var preferCompanyId: String = ""
}