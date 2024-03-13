package com.brody.arxiv.shared.papers.domain.repository

import androidx.paging.PagingData
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import com.brody.arxiv.shared.papers.models.domain.RemoteQuery
import com.brody.arxiv.shared.subjects.models.domain.SubjectNames
import kotlinx.coroutines.flow.Flow

interface PapersRepository {
    suspend fun getPapers(
        query: RemoteQuery,
        subjectNames: SubjectNames
    ): Flow<PagingData<PaperDomainModel>>
}