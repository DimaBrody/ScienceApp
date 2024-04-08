package com.brody.arxiv.shared.settings.ai.data.source

import android.util.Log
import androidx.datastore.core.DataStore
import com.brody.arxiv.shared.settings.ai.data.AiPreferences
import com.brody.arxiv.shared.settings.ai.data.converters.fillDefaults
import com.brody.arxiv.shared.settings.ai.data.converters.toAiDataModel
import com.brody.arxiv.shared.settings.ai.data.converters.toAiPrefsModel
import com.brody.arxiv.shared.settings.ai.data.converters.toConfigPrefsModel
import com.brody.arxiv.shared.settings.ai.data.converters.toModelDataConfig
import com.brody.arxiv.shared.settings.ai.data.copy
import com.brody.arxiv.shared.settings.ai.models.data.ModelKeys
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelConfig
import com.brody.arxiv.shared.settings.ai.models.domain.SettingsAiDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SettingsAiDataSourceImpl @Inject constructor(
    private val settingsAiPreferences: DataStore<AiPreferences>,
    private val securedPreferences: DataStore<ModelKeys>
) : SettingsAiDataSource {

    override val settingsAiData: Flow<SettingsAiDataModel>
        get() = settingsAiPreferences.data.map {
            val settingsAiDataModel = SettingsAiDataModel(
                selectedModel = it.selectedModel.toAiDataModel(),
                modelsConfig = it.modelConfigsMap.mapKeys { entry ->
                    LanguageModel.valueOf(entry.key)
                }.mapValues { entry -> entry.value.toModelDataConfig() }
            )

            if (it.modelConfigsMap.entries.size != 3) {
                updateSettingsAi(
                    settingsAiDataModel.copy(
                        modelsConfig = settingsAiDataModel.modelsConfig.fillDefaults()
                    )
                )
            }

            settingsAiDataModel
        }.distinctUntilChanged()

    override val securedKeysData: Flow<ModelKeys>
        get() = securedPreferences.data

    override suspend fun updateSettingsAi(settingsModel: SettingsAiDataModel) {
        settingsAiPreferences.updateData { aiPrefs ->
            aiPrefs.copy {
                selectedModel = settingsModel.selectedModel.toAiPrefsModel()
                modelConfigs.clear()
                modelConfigs.putAll(
                    settingsModel.modelsConfig.toAiPrefsModel()
                )
            }
        }
    }

    override suspend fun updateConfig(model: LanguageModel, config: LanguageModelConfig) {
        settingsAiPreferences.updateData { aiPrefs ->
            aiPrefs.copy {
                modelConfigs[model.name] = config.toConfigPrefsModel()
            }
        }
    }

    override suspend fun selectModel(model: LanguageModel) {
        settingsAiPreferences.updateData {
            it.copy {
                selectedModel = model.toAiPrefsModel()
            }
        }
    }

    override suspend fun updateKey(model: LanguageModel, key: String): Boolean = try {
        securedPreferences.updateData {
            val currentKeys = it.keys.toMutableMap()
            currentKeys[model] = key

            ModelKeys(currentKeys)
        }
        settingsAiPreferences.updateData {
            it.copy {
                selectedModel = model.toAiPrefsModel()

                val modelConfig = modelConfigs[model.name]

                modelConfig?.let { config ->
                    modelConfigs[model.name] = config.copy {
                        hasKey = true
                    }
                }
            }
        }
        true
    } catch (e: Exception) {
        false
    }
}