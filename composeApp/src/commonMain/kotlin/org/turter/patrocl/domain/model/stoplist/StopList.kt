package org.turter.patrocl.domain.model.stoplist

import org.turter.patrocl.data.dto.stoplist.StopListItemDto

sealed class StopList {

    data class Success(val items: List<StopListItem>): StopList()

    data class Error(val message: String): StopList()

}
