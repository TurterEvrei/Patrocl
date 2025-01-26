package org.turter.patrocl.data.local.impl

import co.touchlab.kermit.Logger
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.turter.patrocl.data.local.config.RealmManager
import org.turter.patrocl.data.local.entity.WaiterLocal
import org.turter.patrocl.data.local.WaiterLocalRepository

class WaiterLocalRepositoryImpl: WaiterLocalRepository {
    private val log = Logger.withTag("WaiterLocalRepositoryImpl")

    private val realm = RealmManager.getRealm()

    override fun get(): Flow<Result<WaiterLocal>> = flow {
        log.d { "Start Entity getAll flow" }
        realm.query<WaiterLocal>()
            .asFlow()
            .collect { res ->
                try {
                    val waiter = res.list.first()
                    log.d { "Fetched Entity from realm: $waiter" }
                    emit(Result.success(waiter))
                } catch (e: Exception) {
                    log.e { "Catch exception while fetching Entity from realm. Exception: $e" }
                    e.printStackTrace()
                    emit(Result.failure(e))
                }
            }
    }

    override suspend fun update(transform: (WaiterLocal) -> WaiterLocal): Result<Unit> {
        log.d { "Start transform waiter" }
        try {
            realm.write {
                val exitingWaiter = query<WaiterLocal>().first().find()

                exitingWaiter?.let { current ->
                    transform(current)
                }
            }
            return Result.success(Unit)
        } catch (e: Exception) {
            log.e { "Catch exception while transform waiter: $e" }
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    override suspend fun replace(data: WaiterLocal) {
        realm.write {
            log.d { "Clean up previous Entity" }
            delete(this.query<WaiterLocal>())
            log.d { "Write to realm Entity: $data" }
            copyToRealm(data)
        }
    }

    override suspend fun cleanUp() {
        log.d { "Start cleanup Entity" }
        realm.write {
            delete(this.query<WaiterLocal>())
        }
    }

}