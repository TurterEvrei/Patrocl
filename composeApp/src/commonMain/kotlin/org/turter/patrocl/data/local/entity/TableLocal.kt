package org.turter.patrocl.data.local.entity

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class TableLocal: RealmObject {
    @PrimaryKey
    var id: String = ""
    var guid: String = ""
    var code: String = ""
    var name: String = ""
    var status: String = ""
    var hall: String = ""
}