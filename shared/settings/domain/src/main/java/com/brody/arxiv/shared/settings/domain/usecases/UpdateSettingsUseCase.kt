package com.brody.arxiv.shared.settings.domain.usecases

import com.brody.arxiv.shared.papers.models.presentation.FetchPapers
import com.brody.arxiv.shared.settings.domain.repository.SettingsRepository
import com.brody.arxiv.shared.settings.models.domain.QueryDataModel
import com.brody.arxiv.shared.settings.models.domain.SettingsDataModel
import com.brody.arxiv.shared.settings.models.domain.toSaveData
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(settingsDataModel: SettingsDataModel) {
        settingsRepository.updateSettings(settingsDataModel)
    }
}