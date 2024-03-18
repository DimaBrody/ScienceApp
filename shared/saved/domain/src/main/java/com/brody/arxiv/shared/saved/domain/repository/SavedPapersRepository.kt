package com.brody.arxiv.shared.saved.domain.repository

import androidx.paging.PagingData
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import kotlinx.coroutines.flow.Flow

interface SavedPapersRepository {
    suspend fun getPapers(): Flow<PagingData<PaperDomainModel>>

    suspend fun removeSaved(id: String)

    suspend fun insertSaved(savePaperData: SaveablePaperDataModel)

    suspend fun getPapersIds(): Flow<List<String>>
}