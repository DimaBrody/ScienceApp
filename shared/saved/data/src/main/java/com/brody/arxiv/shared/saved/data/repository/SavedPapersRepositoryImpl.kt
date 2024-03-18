package com.brody.arxiv.shared.saved.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import com.brody.arxiv.shared.saved.data.SavedPapersPagingSource
import com.brody.arxiv.shared.saved.data.database.SavedPapersDatabase
import com.brody.arxiv.shared.saved.domain.repository.SavedPapersRepository
import com.brody.arxiv.shared.saved.models.data.toEntityModel
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

internal class SavedPapersRepositoryImpl @Inject constructor(
    private val savedPapersDatabase: SavedPapersDatabase
) : SavedPapersRepository {
    private val papersRemotePagingConfig = PagingConfig(
        pageSize = PAGE_SIZE,
        initialLoadSize = INITIAL_LOAD_SIZE
    )

    override suspend fun getPapers(): Flow<PagingData<PaperDomainModel>> =
        Pager(papersRemotePagingConfig) {
            SavedPapersPagingSource(savedPapersDatabase)
        }.flow

    override suspend fun removeSaved(id: String) {
        savedPapersDatabase.papersDao().deletePaperById(id)
    }

    override suspend fun insertSaved(savePaperData: SaveablePaperDataModel) {
        savedPapersDatabase.papersDao().insertPaper(savePaperData.toEntityModel())
    }

    override suspend fun getPapersIds(): Flow<List<String>> =
        savedPapersDatabase.papersDao().getPaperIds().distinctUntilChanged()

    companion object {
        private const val PAGE_SIZE = 0
        private const val INITIAL_LOAD_SIZE = 1
    }
}