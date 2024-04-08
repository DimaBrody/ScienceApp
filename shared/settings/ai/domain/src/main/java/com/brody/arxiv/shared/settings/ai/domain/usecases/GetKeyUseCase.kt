package com.brody.arxiv.shared.settings.ai.domain.usecases

import com.brody.arxiv.shared.settings.ai.domain.repository.SettingsAiRepository
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelKeys
import com.brody.arxiv.shared.settings.ai.models.domain.SettingsAiDataModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import javax.inject.Inject

class GetKeyUseCase @Inject constructor(
    private val settingsAiRepository: SettingsAiRepository
) {
    operator fun invoke(model: LanguageModel): Flow<String?> =
        settingsAiRepository.securedKeysFlow.map { it.keys[model] }
}