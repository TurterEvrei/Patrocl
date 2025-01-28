package org.turter.patrocl.data.local.impl

import co.touchlab.kermit.Logger
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.turter.patrocl.data.local.LocalSource
import org.turter.patrocl.data.local.config.RealmManager
import org.turter.patrocl.data.local.entity.CategoryLocal

class CategoryLocalSource: LocalSource<CategoryLocal> {
    private val log = Logger.withTag("CategoryLocalSource")

    private val realm = RealmManager.getRealm()

    override fun get(): Flow<Result<CategoryLocal>> = flow {
        log.d { "Start Entity getAll flow" }
        realm.query<CategoryLocal>()
            .asFlow()
            .collect { res ->
                try {
                    val category = res.list.first()
                    log.d { "Fetched Entity from realm: $category" }
                    emit(Result.success(category))
                } catch (e: Exception) {
                    log.e { "Catch exception while fetching Entity from realm. Exception: $e" }
                    e.printStackTrace()
                    emit(Result.failure(e))
                }
            }
    }

    override suspend fun replace(data: CategoryLocal) {
        realm.write {
            log.d { "Clean up previous Entity" }
            delete(this.query<CategoryLocal>())
            log.d { "Write to realm Entity: $data" }
            copyToRealm(data)
        }
    }

    override suspend fun cleanUp() {
        log.d { "Start cleanup Entity" }
        realm.write {
            delete(this.query<CategoryLocal>())
        }
    }
}
