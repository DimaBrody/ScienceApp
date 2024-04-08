package com.brody.arxiv.shared.settings.general.models.domain

import com.brody.arxiv.shared.papers.models.presentation.FetchPapers
import com.brody.arxiv.shared.papers.models.presentation.SortBy
import com.brody.arxiv.shared.papers.models.presentation.SortOrder

data class QueryDataModel(
    val sortByOrdinal: Int,
    val sortOrderOrdinal: Int,
    val excludedIds: Set<Int> = emptySet()
)

fun FetchPapers.Remote.Query.toSaveData() = QueryDataModel(
    sortByOrdinal = sortBy?.ordinal ?: 0,
    sortOrderOrdinal = sortOrder?.ordinal ?: 0,
    excludedIds = excludedIds?.toSet() ?: emptySet()
)

fun QueryDataModel.toFetchPapers() = FetchPapers.Remote.Query.Subjects(
    sortBy = SortBy.entries[sortByOrdinal],
    sortOrder = SortOrder.entries[sortOrderOrdinal],
    excludedIds = excludedIds.toList()
)