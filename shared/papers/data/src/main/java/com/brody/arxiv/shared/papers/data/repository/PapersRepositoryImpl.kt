package com.brody.arxiv.shared.papers.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.brody.arxiv.shared.papers.data.PapersPagingSource
import com.brody.arxiv.shared.papers.data.database.PapersDatabase
import com.brody.arxiv.shared.papers.data.remote.source.PapersRemoteDataSource
import com.brody.arxiv.shared.papers.domain.repository.PapersRepository
import com.brody.arxiv.shared.papers.models.data.SubjectNames
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import com.brody.arxiv.shared.papers.models.domain.RemoteQuery
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class PapersRepositoryImpl @Inject constructor(
    private val papersDatabase: PapersDatabase,
    private val papersRemoteDataSource: PapersRemoteDataSource
) : PapersRepository {

    private val papersRemotePagingConfig = PagingConfig(
        pageSize = PAGE_SIZE,
        initialLoadSize = INITIAL_LOAD_SIZE
    )

    override suspend fun getPapers(
        query: RemoteQuery,
        subjectNames: SubjectNames
    ): Flow<PagingData<PaperDomainModel>> =
        Pager(papersRemotePagingConfig) {
            PapersPagingSource(papersDatabase, papersRemoteDataSource, query, subjectNames)
        }.flow


    companion object {
        private const val PAGE_SIZE = 10
        private const val INITIAL_LOAD_SIZE = 10
    }

}