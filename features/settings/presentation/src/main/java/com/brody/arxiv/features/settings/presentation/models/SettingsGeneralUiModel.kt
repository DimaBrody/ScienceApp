package com.brody.arxiv.features.settings.presentation.models

import androidx.compose.runtime.Immutable
import com.brody.arxiv.shared.settings.models.domain.SettingsDataModel

@Immutable
data class SettingsGeneralUiModel(
    private val isSaveSummaries: Boolean,
    private val isSavePdfs: Boolean
) {
    val saveSummaries = SettingsUiOption.SaveSummaries(isSaveSummaries)
    val savePdfs = SettingsUiOption.SavePdfs(isSavePdfs)

    val savedInfoList = listOf(
        saveSummaries,
        savePdfs
    )
}

fun SettingsGeneralUiModel.toSaveModel() =
    SettingsDataModel(
        saveSummaries.value,
        savePdfs.value
    )

fun SettingsDataModel.toPresentationModel() = SettingsGeneralUiModel(
    isSaveSummaries = isSaveSummaries,
    isSavePdfs = isSavePdfs
)


