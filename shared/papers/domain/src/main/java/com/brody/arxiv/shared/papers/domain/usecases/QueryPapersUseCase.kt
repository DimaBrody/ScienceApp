package com.brody.arxiv.shared.papers.domain.usecases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.brody.arxiv.shared.papers.domain.repository.PapersRepository
import com.brody.arxiv.shared.papers.models.domain.FetchPapersDomain
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import com.brody.arxiv.shared.papers.models.domain.RemoteQuery
import com.brody.arxiv.shared.saved.domain.repository.SavedPapersRepository
import com.brody.arxiv.shared.subjects.domain.usecases.GetSubjectNames
import com.brody.arxiv.shared.subjects.models.domain.SubjectsHierarchy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QueryPapersUseCase @Inject constructor(
    private val papersRepository: PapersRepository,
    private val getSubjectNames: GetSubjectNames
) {


    suspend operator fun invoke(
        params: FetchPapersDomain.Remote,
        hierarchy: SubjectsHierarchy? = null
    ): Flow<PagingData<PaperDomainModel>> =
        getSubjectNames.invoke().flatMapLatest {
            papersRepository.getPapers(RemoteQuery.fromParams(params, hierarchy), it)
        }


}