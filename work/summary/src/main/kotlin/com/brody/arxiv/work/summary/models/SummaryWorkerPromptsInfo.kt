package com.brody.arxiv.work.summary.models

import com.brody.arxiv.shared.summary.models.domain.SummaryType
import kotlinx.serialization.Serializable

@Serializable
data class SummaryWorkerPromptsInfo(
    val chunkPrompt: String?,
    val finalPrompt: String?,
    val systemMessage: String?,
    val summaryType: SummaryType
)