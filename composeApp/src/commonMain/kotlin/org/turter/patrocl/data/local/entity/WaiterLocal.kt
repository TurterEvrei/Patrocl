package org.turter.patrocl.data.local.entity

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