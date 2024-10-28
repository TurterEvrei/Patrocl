package org.turter.patrocl.domain.model

sealed class DataStatus {

    data object Initial: DataStatus()

    data object Loading: DataStatus()

    data object Ready: DataStatus()

    data object Empty: DataStatus()

    data class Error(val cause: Throwable): DataStatus()

}