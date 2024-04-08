package com.brody.arxiv.shared.settings.general.domain.usecases

import com.brody.arxiv.shared.settings.general.domain.repository.SettingsRepository
import com.brody.arxiv.shared.settings.general.models.domain.CombinedSettingsDataModel
import com.brody.arxiv.shared.summary.domain.repository.SummaryRepository
import com.brody.arxiv.shared.summary.domain.usecases.GetSummarySettingsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val settingsSummaryUseCase: GetSummarySettingsUseCase
) {
    operator fun invoke(): Flow<CombinedSettingsDataModel> = combine(
        settingsRepository.settingsData,
        settingsSummaryUseCase()
    ) { settingsData, summaryData ->
        CombinedSettingsDataModel(
            settingsData.isSavePdfs,
            summaryData.isSaveSummaries
        )
    }

}