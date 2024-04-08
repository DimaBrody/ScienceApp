package com.brody.arxiv.shared.settings.ai.models.domain

data class LanguageModelConfig(
    val hasKey: Boolean = false,
    val temperature: Float = 0f,
    val isStream: Boolean = true,
    val topP: Float? = null,
    val topK: Int? = null,
    val frequencyPenalty: Float? = null
)