package com.brody.arxiv.shared.settings.ai.domain.usecases

import com.brody.arxiv.shared.settings.ai.domain.repository.SettingsAiRepository
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelConfig
import javax.inject.Inject

class UpdateConfigUseCase @Inject constructor(
    private val settingsAiRepository: SettingsAiRepository
) {
    suspend operator fun invoke(model: LanguageModel, config: LanguageModelConfig) {
        settingsAiRepository.updateConfig(model, config)
    }
}