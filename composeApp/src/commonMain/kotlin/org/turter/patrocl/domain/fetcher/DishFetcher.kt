package org.turter.patrocl.domain.fetcher

import org.turter.patrocl.domain.model.menu.Dish

interface DishFetcher: SourceFetcher<List<Dish>> {
}