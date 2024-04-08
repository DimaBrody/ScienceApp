package com.brody.arxiv.shared.settings.general.models.domain

data class SettingsDataModel(
    val isSavePdfs: Boolean = true
)

data class CombinedSettingsDataModel(
    val isSavePdfs: Boolean,
    val isSaveSummaries: Boolean
)