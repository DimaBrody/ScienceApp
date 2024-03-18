package com.brody.arxiv.shared.settings.domain.usecases

import com.brody.arxiv.shared.settings.domain.repository.SettingsRepository
import com.brody.arxiv.shared.settings.models.domain.QueryDataModel
import com.brody.arxiv.shared.settings.models.domain.SettingsDataModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<SettingsDataModel> =
        settingsRepository.settingsData
}