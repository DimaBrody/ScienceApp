package com.brody.arxiv.shared.settings.ai.data.repository

import com.brody.arxiv.shared.settings.ai.data.di.LangDroidModelFactory
import com.brody.arxiv.shared.settings.ai.domain.repository.SettingsAiRepository
import com.brody.arxiv.shared.settings.ai.data.source.SettingsAiDataSource
import com.brody.arxiv.shared.settings.ai.models.data.toDomainModel
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelConfig
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelKeys
import com.brody.arxiv.shared.settings.ai.models.domain.SettingsAiDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SettingsAiRepositoryImpl @Inject constructor(
    private val settingsAiDataSource: SettingsAiDataSource,
    private val langDroidModelFactory: LangDroidModelFactory
) : SettingsAiRepository {

    override val settingsAiFlow: Flow<SettingsAiDataModel> =
        settingsAiDataSource.settingsAiData

    override val securedKeysFlow: Flow<LanguageModelKeys> =
        settingsAiDataSource.securedKeysData.map { it.toDomainModel() }

    override suspend fun modelSanityCheck(model: LanguageModel, key: String): Boolean =
        langDroidModelFactory.create(model, key).sanityCheck()

    override suspend fun insertNewKey(model: LanguageModel, key: String): Boolean =
        settingsAiDataSource.updateKey(model, key)

    override suspend fun updateConfig(model: LanguageModel, config: LanguageModelConfig) {
        settingsAiDataSource.updateConfig(model, config)
    }

    override suspend fun updateSelectedModel(model: LanguageModel) {
        settingsAiDataSource.selectModel(model)
    }


}