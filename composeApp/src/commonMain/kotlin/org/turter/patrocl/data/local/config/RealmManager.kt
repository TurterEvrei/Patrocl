package org.turter.patrocl.data.local.config

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.turter.patrocl.data.local.entity.CategoryLocal
import org.turter.patrocl.data.local.entity.CompanyEmbeddedLocal
import org.turter.patrocl.data.local.entity.DishLocal
import org.turter.patrocl.data.local.entity.DishModifierLocal
import org.turter.patrocl.data.local.entity.EmployeeLocal
import org.turter.patrocl.data.local.entity.ModifiersGroupLocal
import org.turter.patrocl.data.local.entity.PositionEmbeddedLocal
import org.turter.patrocl.data.local.entity.TableLocal
import org.turter.patrocl.data.local.entity.WaiterLocal

object RealmManager {
    private val config = RealmConfiguration.Builder(
        schema = setOf(
            CategoryLocal::class,
            DishLocal::class,
            DishModifierLocal::class,
            TableLocal::class,
            WaiterLocal::class,
            EmployeeLocal::class,
            PositionEmbeddedLocal::class,
            CompanyEmbeddedLocal::class,
            ModifiersGroupLocal::class
        )
    )
        .compactOnLaunch()
        .build()

    private val instance: Realm by lazy {
        Realm.open(config)
    }

    fun getRealm(): Realm = instance

    fun closeRealm() = instance.close()
}