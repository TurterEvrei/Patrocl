package org.turter.patrocl.data.local

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.turter.patrocl.domain.entity.CategoryLocal
import org.turter.patrocl.domain.entity.CompanyEmbeddedLocal
import org.turter.patrocl.domain.entity.DishLocal
import org.turter.patrocl.domain.entity.DishModifierLocal
import org.turter.patrocl.domain.entity.EmployeeLocal
import org.turter.patrocl.domain.entity.PositionEmbeddedLocal
import org.turter.patrocl.domain.entity.TableLocal
import org.turter.patrocl.domain.entity.WaiterLocal

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
            CompanyEmbeddedLocal::class
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