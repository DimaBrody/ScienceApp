package com.brody.arxiv.shared.summary.domain.usecases

import com.brody.arxiv.shared.summary.domain.repository.SummaryRepository
import com.brody.arxiv.shared.summary.models.domain.SavedSummary
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    operator fun invoke(id: String) : Flow<SavedSummary?> =
        summaryRepository.getSummaryById(id)
}