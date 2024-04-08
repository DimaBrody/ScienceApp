package com.brody.arxiv.shared.summary.domain.usecases

import com.brody.arxiv.shared.summary.domain.repository.SummaryRepository
import com.brody.arxiv.shared.summary.models.domain.SummaryLanguage
import javax.inject.Inject

class UpdateSummaryLanguageUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    suspend operator fun invoke(language: SummaryLanguage) =
        summaryRepository.updateSummaryLanguage(language)
}