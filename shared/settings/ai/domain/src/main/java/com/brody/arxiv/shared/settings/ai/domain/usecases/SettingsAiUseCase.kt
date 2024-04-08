package com.brody.arxiv.shared.settings.ai.domain.usecases

import com.brody.arxiv.shared.settings.ai.domain.repository.SettingsAiRepository
import javax.inject.Inject

class SettingsAiUseCase @Inject constructor(
    private val settingsAiRepository: SettingsAiRepository
) {
    operator fun invoke() = settingsAiRepository.settingsAiFlow
}