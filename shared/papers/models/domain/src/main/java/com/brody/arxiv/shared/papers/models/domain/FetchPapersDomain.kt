package com.brody.arxiv.shared.papers.models.domain

sealed interface FetchPapersDomain {
    sealed interface Remote : FetchPapersDomain {
        data object Offline : Remote
        sealed class Query(
            open val sortBy: StringParam?,
            open val sortOrder: StringParam?,
            open val excludedIds: List<Int>?,
            open val prefixParams: List<StringParam>?

        ) : Remote {
            class Default(
                override val sortBy: StringParam? = null,
                override val sortOrder: StringParam? = null,
                override val prefixParams: List<StringParam>? = null
            ) : Query(sortBy, sortOrder, null, prefixParams)

            class Subjects(
                override val sortBy: StringParam? = null,
                override val sortOrder: StringParam? = null,
                override val excludedIds: List<Int>? = null,
                override val prefixParams: List<StringParam>? = null
            ) : Query(sortBy, sortOrder, excludedIds, prefixParams)
        }
    }

    data object Saved : FetchPapersDomain
}

