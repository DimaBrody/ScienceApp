package com.brody.arxiv.shared.settings.models.domain

data class SettingsDataModel(
    val isSaveSummaries: Boolean = true,
    val isSavePdfs: Boolean = true
)