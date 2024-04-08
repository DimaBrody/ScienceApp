package com.brody.arxiv.shared.summary.domain.usecases

import com.brody.arxiv.shared.summary.domain.repository.SummaryRepository
import javax.inject.Inject

class UpdateSaveSummariesUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(isSaveSummary: Boolean) =
        summaryRepository.updateSaveSummaries(isSaveSummary)
}