package com.brody.arxiv.shared.summary.domain.usecases

import com.brody.arxiv.shared.saved.domain.usecases.ToggleSaveItemUseCase
import com.brody.arxiv.shared.saved.models.domain.SaveablePaperDataModel
import com.brody.arxiv.shared.summary.domain.repository.SummaryRepository
import com.brody.arxiv.shared.summary.models.domain.SummaryType
import com.langdroid.core.LangDroidModel
import javax.inject.Inject

class GenerateSummaryUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository,
) {
    operator fun invoke(
        model: LangDroidModel<*>,
        text: String,
        isStream: Boolean? = null,
        chunkPrompt: String? = null,
        finalPrompt: String? = null,
        systemMessage: String? = null
    ) = summaryRepository.generateSummary(
        model = model,
        isStream = isStream ?: true,
        text = text,
        chunkPrompt = chunkPrompt,
        finalPrompt = finalPrompt,
        systemMessage = systemMessage
    )
}