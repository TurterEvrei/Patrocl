package org.turter.patrocl.domain.entity

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject

class EmployeeLocal : RealmObject {
    var id: String = ""
    var name: String = ""
    var lastName: String = ""
    var patronymic: String = ""
    var simpleName: String = ""
    var active: Boolean = false
    var position: PositionEmbeddedLocal? = null
    var userId: String = ""
    var companyList: RealmList<CompanyEmbeddedLocal> = realmListOf()
}

class CompanyEmbeddedLocal : RealmObject {
    var id: String = ""
    var title: String = ""
}

class PositionEmbeddedLocal : RealmObject {
    var id: String = ""
    var title: String = ""
    var specialization: String = ""
    var rankWeight: Int = 0
}