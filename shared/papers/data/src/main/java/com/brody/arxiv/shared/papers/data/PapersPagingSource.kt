package com.brody.arxiv.shared.papers.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.brody.arxiv.core.common.extensions.mapNotNullEmpty
import com.brody.arxiv.shared.papers.data.database.PapersDatabase
import com.brody.arxiv.shared.papers.data.remote.source.PapersRemoteDataSource
import com.brody.arxiv.shared.papers.domain.repository.PapersRepository
import com.brody.arxiv.shared.papers.models.data.SubjectNames
import com.brody.arxiv.shared.papers.models.data.toDomainModel
import com.brody.arxiv.shared.papers.models.data.toEntityModel
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import com.brody.arxiv.shared.papers.models.domain.RemoteQuery
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

internal class PapersPagingSource(
    private val papersDatabase: PapersDatabase,
    private val papersDataSource: PapersRemoteDataSource,
    private val query: RemoteQuery,
    private val subjectNames: SubjectNames
) : PagingSource<Int, PaperDomainModel>() {

    // Idea: load 1st page to database, return current list if online, from database if offline
    // SSOT is this class, not Database, even during offline, this class handles what to return depending on params
    override fun getRefreshKey(state: PagingState<Int, PaperDomainModel>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PaperDomainModel> {
        return when (query) {
            is RemoteQuery.Offline -> {
                val data = papersDatabase.papersDao()
                    .getPaperEntities().first().map { it.toDomainModel(subjectNames) }

                LoadResult.Page(
                    data = data,
                    prevKey = null,
                    nextKey = null
                )
            }

            is RemoteQuery.Search -> {
                return try {
                    val page: Int = params.key ?: 0

                    val limit: Int = params.loadSize


                    val urlQuery = query.createUrl(limit, page)
                    val data = papersDataSource.getPapers(urlQuery)

                    Log.d("HELLO", "BEFORE")
                    if (page == 0) {
                        data.entry?.let { entries ->
                            papersDatabase.papersDao()
                                .replaceAll(entries.map { it.toEntityModel() })
                        }
                    }
                    Log.d("HELLO", "AFTER")

                    val nextPage = if (data.entry.isNullOrEmpty()) {
                        null
                    } else {
                        page + 1
                    }

                    val prevPage = if (page == 0) null else page - 1
                    LoadResult.Page(
                        data = data.entry.mapNotNullEmpty { it.toDomainModel(subjectNames) },
                        prevKey = prevPage,
                        nextKey = nextPage
                    )
                } catch (ex: HttpException) {
                    ex.printStackTrace()
                    LoadResult.Error(ex)
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    LoadResult.Error(ex)
                }
            }
        }
    }
}