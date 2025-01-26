package org.turter.patrocl.domain.fetcher

import org.turter.patrocl.domain.model.source.Table

interface TableFetcher: SourceFetcher<List<Table>> {
}