package com.brody.arxiv.shared.summary.models.domain

import kotlinx.serialization.Serializable

data class SettingsSummaryDataModel(
    val isSaveSummaries: Boolean = true,
    val summaryType: SummaryType = SummaryType.FAN,
    val summaryLanguage: SummaryLanguage = SummaryLanguage.ENGLISH
)

@Serializable
enum class SummaryType {
    FAN, STUDENT, EXPERT
}

enum class SummaryLanguage {
    ENGLISH, UKRAINIAN, RUSSIAN
}
