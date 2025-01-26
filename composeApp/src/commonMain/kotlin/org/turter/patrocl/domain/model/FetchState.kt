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

//        fun isAllSuccess(dataArray: Array<FetchState<Any>>) = dataArray.all { it.isSuccess() }
//        fun isAllSuccess(vararg state: FetchState<Any>) = state.all { it.isSuccess() }
//        fun isAnyLoadingOrInitial(dataArray: Array<FetchState<Any>>) =
//            dataArray.any { it.isLoadingOrInitial() }
//        fun isAnyError(dataArray: Array<FetchState<Any>>) = dataArray.any { it.isError() }
    }

    fun isSuccess() = this is Finished && this.result.isSuccess
    fun isLoadingOrInitial() = this is Loading || this is Initial
    fun isError() = this is Finished && this.result.isFailure

    fun takeIfSuccess(): T? = if (this is Finished) this.result.getOrNull() else null

//    fun getOrThrow(): T = (this as Finished).result.getOrThrow()

}