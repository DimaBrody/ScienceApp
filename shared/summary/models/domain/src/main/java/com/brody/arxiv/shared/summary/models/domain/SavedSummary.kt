package com.brody.arxiv.shared.summary.models.domain

data class SavedSummary(
    val id: String,
    val summaries: Map<SummaryType, String>
)

