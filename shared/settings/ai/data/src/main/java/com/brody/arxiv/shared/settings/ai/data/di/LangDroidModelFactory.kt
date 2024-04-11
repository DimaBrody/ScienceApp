package com.brody.arxiv.shared.settings.ai.data.di

import com.brody.arxiv.shared.settings.ai.models.domain.LanguageModel
import com.langdroid.core.LangDroidModel

interface LangDroidModelFactory {
    fun create(model: LanguageModel, key: String): LangDroidModel<*>
}