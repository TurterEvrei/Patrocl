package org.turter.patrocl.domain.model.stoplist

sealed class StopList {

    data class Success(val items: List<StopListItem>): StopList()

    data class Error(val message: String): StopList()

}
