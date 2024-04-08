package com.brody.arxiv.shared.settings.general.domain.usecases

import com.brody.arxiv.shared.settings.general.domain.repository.SettingsRepository
import com.brody.arxiv.shared.settings.general.models.domain.QueryDataModel
import javax.inject.Inject

class UpdateQueryPrefUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(queryDataModel: QueryDataModel) {
        settingsRepository.updateQuery(queryDataModel)
    }
}