package org.turter.patrocl.domain.model

//TODO delete?
sealed class RemoteSourceStatus {

    object Initial: RemoteSourceStatus()

    object Available: RemoteSourceStatus()

    data class Unavailable(val cause: Throwable): RemoteSourceStatus()

}