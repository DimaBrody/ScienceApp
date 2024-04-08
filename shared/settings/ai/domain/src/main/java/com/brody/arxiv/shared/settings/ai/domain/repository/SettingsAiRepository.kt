package com.brody.arxiv.shared.settings.ai.domain.repository

import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelConfig
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelKeys
import com.brody.arxiv.shared.settings.ai.models.domain.SettingsAiDataModel
import kotlinx.coroutines.flow.Flow

interface SettingsAiRepository {

    val settingsAiFlow: Flow<SettingsAiDataModel>

    val securedKeysFlow: Flow<LanguageModelKeys>

    suspend fun modelSanityCheck(model: LanguageModel, key: String): Boolean

    suspend fun insertNewKey(model: LanguageModel, key: String): Boolean

    suspend fun updateConfig(model: LanguageModel, config: LanguageModelConfig)

    suspend fun updateSelectedModel(model: LanguageModel)
}