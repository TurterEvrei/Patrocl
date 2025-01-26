package org.turter.patrocl.domain.fetcher

import org.turter.patrocl.domain.model.menu.DishModifier

interface ModifiersFetcher: SourceFetcher<List<DishModifier>> {
}