package com.brody.arxiv.shared.summary.domain.usecases

import com.brody.arxiv.shared.summary.domain.repository.SummaryRepository
import com.brody.arxiv.shared.summary.models.domain.SummaryType
import javax.inject.Inject

class UpdateSummaryTypeUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(summaryType: SummaryType) =
        summaryRepository.updateSummaryType(summaryType)
}