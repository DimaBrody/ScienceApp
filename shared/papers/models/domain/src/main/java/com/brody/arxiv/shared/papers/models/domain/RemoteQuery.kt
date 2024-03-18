package com.brody.arxiv.shared.papers.models.domain

import android.util.Log
import com.brody.arxiv.shared.subjects.models.domain.SubjectsHierarchy

private const val AND_NOT = "+ANDNOT+"
private const val OR = "+OR+"

sealed interface RemoteQuery {

    data object Offline : RemoteQuery

    class Search(
        private val queryRemote: FetchPapersDomain.Remote.Query,
        private val subjectsHierarchy: SubjectsHierarchy?
    ) : RemoteQuery {
        fun createUrl(limit: Int, page: Int): String {
            val sb = StringBuilder("/api/query?search_query=")

            var isFirstPrefix = true

            val prefixParams: MutableList<PrefixParams> =
                queryRemote.prefixParams?.map { PrefixParams(OR, it.convertToString()) }
                    ?.toMutableList()
                    ?: mutableListOf()

            subjectsHierarchy?.let { hierarchy ->
                val ids = hierarchy.getSelectedIds(excludedIds = queryRemote.excludedIds)
                val selectedSubjects = ids.first
                val excludedIdsSubjects = ids.second

                prefixParams.addAll(selectedSubjects.map { PrefixParams(OR, it) })
                prefixParams.addAll(excludedIdsSubjects.map { PrefixParams(AND_NOT, it) })
            }

//            Log.d("HELLOREM", prefixParams.toString())
            prefixParams.let { prefixes ->
                for (prefix in prefixes) {
                    if (isFirstPrefix && prefix.selector == AND_NOT)
                        return@let
                    if (!isFirstPrefix)
                        sb.append(prefix.selector)

                    isFirstPrefix = false

                    sb.append(prefix.value)
                }
            }

            queryRemote.sortBy?.let {
                sb.append("&sortBy=${it.convertToString()}")
            }

            queryRemote.sortOrder?.let {
                sb.append("&sortOrder=${it.convertToString()}")
            }

            sb.append("&start=${page * limit}&max_results=$limit")

            return sb.toString()
        }
    }

    companion object {
        fun fromParams(
            queryRemote: FetchPapersDomain.Remote, subjectsHierarchy: SubjectsHierarchy? = null
        ): RemoteQuery {
            return when (queryRemote) {
                is FetchPapersDomain.Remote.Offline -> Offline
                is FetchPapersDomain.Remote.Query -> Search(queryRemote, subjectsHierarchy)
            }
        }
    }
}

private data class PrefixParams(
    val selector: String,
    val value: String
)