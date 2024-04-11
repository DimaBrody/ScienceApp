package com.brody.arxiv.shared.settings.ai.models.data

import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.langdroid.core.models.GenerativeModel
import com.langdroid.core.models.gemini.GeminiModel
import com.langdroid.core.models.openai.OpenAiModel

fun LanguageModel.toLangdroidModel(apiKey: String): GenerativeModel = when (this) {
    LanguageModel.GEMINI -> GeminiModel.Pro(apiKey)
    LanguageModel.OPENAI -> OpenAiModel.Gpt3_5Plus(apiKey)
    LanguageModel.CUSTOM -> OpenAiModel.Gpt4(apiKey)
    else -> throw NoSelectedModelException()
}

class NoSelectedModelException(override val message: String? = "Select model before transforming to LangDroid model") :
    Exception(message)