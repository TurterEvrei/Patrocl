package org.turter.patrocl.data.local.impl

import co.touchlab.kermit.Logger
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.turter.patrocl.data.local.LocalSource
import org.turter.patrocl.data.local.config.RealmManager
import org.turter.patrocl.data.local.entity.ModifiersGroupLocal

class ModifiersGroupLocalSource: LocalSource<ModifiersGroupLocal> {
    private val log = Logger.withTag("ModifiersGroupLocalSource")

    private val realm = RealmManager.getRealm()

    override fun get(): Flow<Result<ModifiersGroupLocal>> = flow {
        log.d { "Start Entity getAll flow" }
        realm.query<ModifiersGroupLocal>()
            .asFlow()
            .collect { res ->
                try {
                    val group = res.list.first()
                    log.d { "Fetched Entity from realm: $group" }
                    emit(Result.success(group))
                } catch (e: Exception) {
                    log.e { "Catch exception while fetching Entity from realm. Exception: $e" }
                    e.printStackTrace()
                    emit(Result.failure(e))
                }
            }
    }

    override suspend fun cleanUp() {
        log.d { "Start cleanup Entity" }
        realm.write {
            delete(this.query<ModifiersGroupLocal>())
        }
    }

    override suspend fun replace(data: ModifiersGroupLocal) {
        realm.write {
            log.d { "Clean up previous Entity" }
            delete(this.query<ModifiersGroupLocal>())
            log.d { "Write to realm Entity: $data" }
            copyToRealm(data)
        }
    }
}