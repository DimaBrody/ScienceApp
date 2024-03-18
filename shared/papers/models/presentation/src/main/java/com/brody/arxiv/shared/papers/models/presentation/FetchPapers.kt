package com.brody.arxiv.shared.papers.models.presentation

import com.brody.arxiv.shared.papers.models.domain.FetchPapersDomain

sealed interface FetchPapers {
    sealed interface Remote : FetchPapers {
        data object Offline : Remote
        sealed class Query(
            open val sortBy: SortBy?,
            open val sortOrder: SortOrder?,
            open val excludedIds: List<Int>?,
            open val prefixParams: List<PrefixParam>?

        ) : Remote {
            data class Default(
                override val sortBy: SortBy? = null,
                override val sortOrder: SortOrder? = null,
                override val prefixParams: List<PrefixParam>? = null
            ) : Query(sortBy, sortOrder, null, prefixParams)

            data class Subjects(
                override val sortBy: SortBy? = null,
                override val sortOrder: SortOrder? = null,
                override val excludedIds: List<Int>? = null,
                override val prefixParams: List<PrefixParam>? = null
            ) : Query(sortBy, sortOrder, excludedIds, prefixParams)
        }
    }

    data object Saved : FetchPapers



//    fun checkEquals(other: FetchPapers?): Boolean {
//        return if (other == null) false
//        else {
//            when {
//                other is Remote.Query && this is Remote.Query -> {
//                    other.excludedIds == this.excludedIds && other.sortBy == this.sortBy
//                            && other.sortOrder == this.sortOrder &&
//                            other.prefixParams == this.prefixParams
//                }
//
//                else -> (other == this)
//            }
//        }
//    }
}

fun FetchPapers.toDomainRequest(): FetchPapersDomain {
    return when (this) {
        is FetchPapers.Remote.Offline -> FetchPapersDomain.Remote.Offline
        is FetchPapers.Remote.Query.Default -> FetchPapersDomain.Remote.Query.Default(
            sortBy = sortBy,
            sortOrder = sortOrder,
            prefixParams = prefixParams
        )

        is FetchPapers.Remote.Query.Subjects -> FetchPapersDomain.Remote.Query.Subjects(
            sortBy = sortBy,
            sortOrder = sortOrder,
            prefixParams = prefixParams,
            excludedIds = excludedIds
        )

        is FetchPapers.Saved -> FetchPapersDomain.Saved
    }
}

