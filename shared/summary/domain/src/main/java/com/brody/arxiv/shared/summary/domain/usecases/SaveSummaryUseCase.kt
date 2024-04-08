package com.brody.arxiv.shared.summary.domain.usecases

import com.brody.arxiv.shared.saved.domain.usecases.ToggleSaveItemUseCase
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import com.brody.arxiv.shared.summary.domain.repository.SummaryRepository
import com.brody.arxiv.shared.summary.models.domain.SummaryType
import javax.inject.Inject

class SaveSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository,
    private val toggleSaveItemUseCase: ToggleSaveItemUseCase
) {
    suspend operator fun invoke(type: SummaryType, summary: String, model: SaveablePaperDataModel) {
        summaryRepository.saveSummary(type, summary, model.id)
        toggleSaveItemUseCase(model.id, model.also { it.hasSummaries = true })
    }
}