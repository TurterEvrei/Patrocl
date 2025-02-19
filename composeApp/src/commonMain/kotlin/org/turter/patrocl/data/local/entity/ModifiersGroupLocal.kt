package org.turter.patrocl.data.local.entity

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class ModifiersGroupLocal: RealmObject {
    @PrimaryKey
    var id: String = ""
    var guid: String = ""
    var code: String = ""
    var name: String = ""
    var status: String = ""
    var mainParentIdent: String = ""
    var childList: RealmList<ModifiersGroupLocal> = realmListOf()
    var modifierIdList: RealmList<String> = realmListOf()
}