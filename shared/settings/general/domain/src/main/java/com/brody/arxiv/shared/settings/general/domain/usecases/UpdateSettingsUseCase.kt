package com.brody.arxiv.shared.settings.general.domain.usecases

import com.brody.arxiv.shared.settings.general.domain.repository.SettingsRepository
import com.brody.arxiv.shared.settings.general.models.domain.CombinedSettingsDataModel
import com.brody.arxiv.shared.settings.general.models.domain.SettingsDataModel
import com.brody.arxiv.shared.summary.domain.usecases.UpdateSaveSummariesUseCase
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val updateSaveSummariesUseCase: UpdateSaveSummariesUseCase
) {
    suspend operator fun invoke(combinedSettingsDataModel: CombinedSettingsDataModel) {
        settingsRepository.updateSettings(SettingsDataModel(isSavePdfs = combinedSettingsDataModel.isSavePdfs))
        updateSaveSummariesUseCase(combinedSettingsDataModel.isSaveSummaries)
    }
}