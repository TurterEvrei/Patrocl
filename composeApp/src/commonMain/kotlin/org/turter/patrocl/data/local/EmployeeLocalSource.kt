package org.turter.patrocl.data.local

import co.touchlab.kermit.Logger
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.turter.patrocl.domain.entity.EmployeeLocal
import org.turter.patrocl.domain.repository.LocalSource

class EmployeeLocalSource: LocalSource<EmployeeLocal> {
    private val log = Logger.withTag("EmployeeLocalSource")

    private val realm = RealmManager.getRealm()

    override fun get(): Flow<Result<EmployeeLocal>> = flow {
        log.d { "Start Entity getAll flow" }
        realm.query<EmployeeLocal>()
            .asFlow()
            .collect { res ->
                try {
                    val employee = res.list.first()
                    log.d { "Fetched Entity from realm: $employee" }
                    emit(Result.success(employee))
                } catch (e: Exception) {
                    log.e { "Catch exception while fetching Entity from realm. Exception: $e" }
                    e.printStackTrace()
                    emit(Result.failure(e))
                }
            }
    }

    override suspend fun replace(data: EmployeeLocal) {
        log.d { "Clean up previous Entity" }
        cleanUp()
        log.d { "Write to realm Entity: $data" }
        realm.write {
            copyToRealm(data)
        }
    }

    override suspend fun cleanUp() {
        log.d { "Start cleanup Entity" }
        realm.write {
            delete(this.query<EmployeeLocal>())
        }
    }
}