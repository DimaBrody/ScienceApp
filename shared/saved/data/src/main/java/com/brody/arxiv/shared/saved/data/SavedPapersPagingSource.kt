package com.brody.arxiv.shared.saved.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import com.brody.arxiv.shared.saved.data.database.SavedPapersDatabase
import com.brody.arxiv.shared.saved.models.data.toDomainModel
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import java.io.IOException

internal class SavedPapersPagingSource(
    private val savedDatabase: SavedPapersDatabase
) : PagingSource<Int, PaperDomainModel>() {

    // Idea: load 1st page to database, return current list if online, from database if offline
    // SSOT is this class, not Database, even during offline, this class handles what to return depending on params
    override fun getRefreshKey(state: PagingState<Int, PaperDomainModel>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PaperDomainModel> {
        return try {
            val data = savedDatabase.papersDao()
                .getSavedPaperEntities().first().map { it.toDomainModel() }

            LoadResult.Page(
                data = data, prevKey = null, nextKey = null
            )
        } catch (ex: HttpException) {
            LoadResult.Error(ex)
        } catch (ex: IOException) {
            LoadResult.Error(ex)
        }
    }
}