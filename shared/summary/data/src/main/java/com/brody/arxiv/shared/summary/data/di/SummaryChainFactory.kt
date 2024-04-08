package com.brody.arxiv.shared.summary.data.di

import com.brody.langdroid.core.LangDroidModel
import com.brody.summary.SummaryChain
import com.brody.summary.prompts.PromptTemplate

interface SummaryChainFactory {
    fun create(
        model: LangDroidModel<*>,
        isStream: Boolean,
        chunkPrompt: String?,
        finalPrompt: String?,
        systemMessage: String?
    ): SummaryChain<*>
}