package com.brody.arxiv.shared.settings.ai.domain.usecases

import com.brody.arxiv.shared.settings.ai.domain.repository.SettingsAiRepository
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import javax.inject.Inject

class UpdateSelectedModelUseCase @Inject constructor(
    private val settingsAiRepository: SettingsAiRepository
) {
    suspend operator fun invoke(model: LanguageModel) {
        settingsAiRepository.updateSelectedModel(model)
    }
}