package org.turter.patrocl.domain.model

sealed class BindStatus {

    data object Initial: BindStatus()

    data object Loading: BindStatus()

    data object Bind: BindStatus()

    data object NotBind: BindStatus()

}