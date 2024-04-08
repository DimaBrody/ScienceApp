package com.brody.arxiv.shared.settings.ai.domain.usecases

import com.brody.arxiv.shared.settings.ai.domain.repository.SettingsAiRepository
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import javax.inject.Inject

class TrySetNewKeyUseCase @Inject constructor(
    private val settingsAiRepository: SettingsAiRepository,
    private val updateSelectedModel: UpdateSelectedModelUseCase
) {
    suspend operator fun invoke(model: LanguageModel, key: String): Boolean {
        val sanityCheck = settingsAiRepository.modelSanityCheck(model, key)
        if (!sanityCheck) return false

        val isKeyInserted = settingsAiRepository.insertNewKey(model, key)
        if (isKeyInserted) updateSelectedModel(model)

        return isKeyInserted
    }
}