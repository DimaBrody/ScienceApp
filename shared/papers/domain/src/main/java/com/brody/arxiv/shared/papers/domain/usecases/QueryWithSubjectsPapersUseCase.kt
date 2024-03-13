package com.brody.arxiv.shared.papers.domain.usecases

import androidx.paging.PagingData
import com.brody.arxiv.shared.papers.models.domain.FetchPapersDomain
import com.brody.arxiv.shared.papers.models.domain.PaperDomainModel
import com.brody.arxiv.shared.subjects.domain.repository.SubjectsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class QueryWithSubjectsPapersUseCase @Inject constructor(
    private val queryPapersUseCase: QueryPapersUseCase,
    private val subjectsRepository: SubjectsRepository
) {
    suspend operator fun invoke(params: FetchPapersDomain.Remote): Flow<PagingData<PaperDomainModel>> =
        subjectsRepository.allUserSubjects.flatMapLatest { hierarchy ->
            queryPapersUseCase.invoke(params, hierarchy)
        }
}