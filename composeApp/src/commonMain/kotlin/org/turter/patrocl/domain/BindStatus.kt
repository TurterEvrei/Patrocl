package org.turter.patrocl.domain

sealed class BindStatus {

    data object Initial: BindStatus()

    data object Loading: BindStatus()

    data object Bind: BindStatus()

    data object NotBind: BindStatus()

}