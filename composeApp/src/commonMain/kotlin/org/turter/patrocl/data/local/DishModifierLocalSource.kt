package org.turter.patrocl.data.local

import co.touchlab.kermit.Logger
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.turter.patrocl.domain.entity.DishModifierLocal
import org.turter.patrocl.domain.exception.EmptyLocalDataException
import org.turter.patrocl.domain.repository.LocalSource

class DishModifierLocalSource: LocalSource<List<DishModifierLocal>> {
    private val log = Logger.withTag("DishModifierLocalSource")

    private val realm = RealmManager.getRealm()

    override fun get(): Flow<Result<List<DishModifierLocal>>> = flow {
        log.d { "Start Entity getAll flow" }
        realm.query<DishModifierLocal>()
            .asFlow()
            .collect { res ->
                try {
                    val entities = res.list
                    log.d { "Fetched Entity from realm: $entities" }
                    emit(
                        if (entities.isNotEmpty()) Result.success(entities)
                        else Result.failure(EmptyLocalDataException())
                    )
                } catch (e: Exception) {
                    log.e { "Catch exception while fetching Entity from realm. Exception: $e" }
                    e.printStackTrace()
                    emit(Result.failure(e))
                }
            }
    }

    override suspend fun replace(data: List<DishModifierLocal>) {
        log.d { "Clean up previous Entity" }
        cleanUp()
        log.d { "Write to realm Entity: $data" }
        data.forEach { element ->
            realm.write {
                copyToRealm(element)
            }
        }
    }

    override suspend fun cleanUp() {
        log.d { "Start cleanup Entity" }
        realm.write {
            delete(this.query<DishModifierLocal>())
        }
    }
}