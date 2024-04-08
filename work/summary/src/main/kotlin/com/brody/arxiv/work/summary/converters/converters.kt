package com.brody.arxiv.work.summary.converters

import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModelConfig
import com.brody.langdroid.core.models.GenerativeModel
import com.brody.langdroid.core.models.request.config.GenerativeConfig

inline fun <reified M : GenerativeModel> LanguageModelConfig.toLangdroidConfig() =
    GenerativeConfig.create<M> {
        temperature = this@toLangdroidConfig.temperature
        topK = this@toLangdroidConfig.topK
        topP = this@toLangdroidConfig.topP
    }