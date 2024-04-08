package com.brody.arxiv.shared.settings.ai.data.source

import com.brody.arxiv.shared.settings.ai.data.ModelConfig
import com.brody.arxiv.shared.settings.ai.models.data.ModelKeys
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelConfig
import com.brody.arxiv.shared.settings.ai.models.domain.SettingsAiDataModel
import kotlinx.coroutines.flow.Flow


interface SettingsAiDataSource {
    val settingsAiData: Flow<SettingsAiDataModel>

    val securedKeysData: Flow<ModelKeys>

    suspend fun updateSettingsAi(settingsModel: SettingsAiDataModel)

    suspend fun updateConfig(model: LanguageModel, config: LanguageModelConfig)

    suspend fun selectModel(model: LanguageModel)

    suspend fun updateKey(model: LanguageModel, key: String): Boolean
}