package com.brody.arxiv.shared.settings.ai.models.presentation

import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel

enum class LanguageUiModel {
    GEMINI, OPENAI, CUSTOM;

    fun uiName(): String = when (this) {
        GEMINI -> "Gemini"
        OPENAI -> "OpenAI"
        CUSTOM -> "Custom"
    }
}

fun LanguageUiModel.toDomainModel() = LanguageModel.valueOf(name)

fun LanguageModel.toPresentationModel() = LanguageUiModel.valueOf(name)
