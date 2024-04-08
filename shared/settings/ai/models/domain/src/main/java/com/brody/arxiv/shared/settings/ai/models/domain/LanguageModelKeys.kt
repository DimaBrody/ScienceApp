package com.brody.arxiv.shared.settings.ai.models.domain

import java.util.EnumMap

data class LanguageModelKeys(
    val keys: Map<LanguageModel, String?>
) {
    companion object {
        fun createDefault() = LanguageModelKeys(EnumMap(LanguageModel::class.java))
    }
}