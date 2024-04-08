package com.brody.arxiv.features.settings.presentation.models

import androidx.compose.runtime.Immutable
import com.brody.arxiv.shared.settings.general.models.domain.CombinedSettingsDataModel
import com.brody.arxiv.shared.settings.general.models.domain.SettingsDataModel
import com.brody.arxiv.shared.summary.models.domain.SettingsSummaryDataModel

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
    CombinedSettingsDataModel(
        saveSummaries.value,
        savePdfs.value
    )

fun CombinedSettingsDataModel.toPresentationModel() = SettingsGeneralUiModel(
    isSavePdfs = isSavePdfs,
    isSaveSummaries = isSaveSummaries
)
