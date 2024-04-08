package com.brody.arxiv.shared.settings.ai.models.domain

data class SettingsAiDataModel(
    val selectedModel: LanguageModel = LanguageModel.NOTHING,
//    map<int32, ModelConfig> modelConfigs = 2;
    val modelsConfig: Map<LanguageModel, LanguageModelConfig>
)