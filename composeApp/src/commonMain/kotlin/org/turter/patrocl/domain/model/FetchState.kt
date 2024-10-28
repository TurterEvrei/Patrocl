package org.turter.patrocl.domain.model

sealed class FetchState<out T> {

    data object Initial: FetchState<Nothing>()
    data object Loading: FetchState<Nothing>()
    data class Finished<T>(val result: Result<T>): FetchState<T>()

    companion object Factory {
        fun <T> initial(): FetchState<T> = Initial
        fun <T> loading(): FetchState<T> = Loading
        fun <T> done(result: Result<T>): FetchState<T> = Finished(result)
        fun <T> success(data: T): FetchState<T> = Finished(Result.success(data))
        fun <T> fail(e: Throwable): FetchState<T> = Finished(Result.failure(e))
    }

    fun isSuccess() = this is Finished && this.result.isSuccess

}