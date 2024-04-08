package com.brody.arxiv.shared.settings.ai.data.converters

import com.brody.arxiv.core.data.proto.toFloat
import com.brody.arxiv.core.data.proto.toInt
import com.brody.arxiv.shared.settings.ai.data.AiModel
import com.brody.arxiv.shared.settings.ai.data.ModelConfig
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelConfig
import com.google.protobuf.FloatValue
import com.google.protobuf.Int32Value

fun AiModel.toAiDataModel(): LanguageModel =
    LanguageModel.valueOf(this.name)

fun LanguageModel.toAiPrefsModel(): AiModel =
    AiModel.valueOf(this.name)

fun ModelConfig.toModelDataConfig(): LanguageModelConfig =
    LanguageModelConfig(
        hasKey = hasKey,
        temperature = temperature.toFloat() ?: 0f,
        isStream = !isNoStream,
        topP = topP.toFloat(),
        topK = topK.toInt(),
        frequencyPenalty = frequencyPenalty.toFloat()
    )

fun LanguageModelConfig.toConfigPrefsModel(): ModelConfig = ModelConfig.newBuilder().apply {
    hasKey = this@toConfigPrefsModel.hasKey
    temperature = FloatValue.of(this@toConfigPrefsModel.temperature)
    isNoStream = !this@toConfigPrefsModel.isStream

    this@toConfigPrefsModel.topP?.let {
        topP = FloatValue.of(it)
    }

    this@toConfigPrefsModel.topK?.let {
        topK = Int32Value.of(it)
    }

    this@toConfigPrefsModel.frequencyPenalty?.let {
        frequencyPenalty = FloatValue.of(it)
    }
}.build()

fun Map<LanguageModel, LanguageModelConfig>.toAiPrefsModel(): Map<String, ModelConfig> =
    mapKeys { it.key.name }.mapValues { entry -> entry.value.toConfigPrefsModel() }

fun Map<LanguageModel, LanguageModelConfig>.fillDefaults(): Map<LanguageModel, LanguageModelConfig> {
    val mutableMap = this.toMutableMap()
    mutableMap.fillForLanguageModel(LanguageModel.GEMINI)
    mutableMap.fillForLanguageModel(LanguageModel.OPENAI)
    mutableMap.fillForLanguageModel(LanguageModel.CUSTOM)
    return mutableMap
}

private fun MutableMap<LanguageModel, LanguageModelConfig>.fillForLanguageModel(model: LanguageModel) {
    get(model) ?: let { this[model] = LanguageModelConfig() }
}

