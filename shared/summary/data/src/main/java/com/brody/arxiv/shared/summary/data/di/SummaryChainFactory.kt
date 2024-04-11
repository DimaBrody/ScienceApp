package com.brody.arxiv.shared.summary.data.di

import com.langdroid.core.LangDroidModel
import com.langdroid.summary.SummaryChain

interface SummaryChainFactory {
    fun create(
        model: LangDroidModel<*>,
        isStream: Boolean,
        chunkPrompt: String?,
        finalPrompt: String?,
        systemMessage: String?
    ): SummaryChain<*>
}