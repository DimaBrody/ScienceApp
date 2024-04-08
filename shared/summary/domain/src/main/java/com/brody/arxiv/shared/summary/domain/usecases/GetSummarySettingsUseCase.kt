package com.brody.arxiv.shared.summary.domain.usecases

import com.brody.arxiv.shared.summary.domain.repository.SummaryRepository
import com.brody.arxiv.shared.summary.models.domain.SavedSummary
import com.brody.arxiv.shared.summary.models.domain.SettingsSummaryDataModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSummarySettingsUseCase @Inject constructor(
    private val summaryRepository: SummaryRepository
) {
    operator fun invoke(): Flow<SettingsSummaryDataModel> =
        summaryRepository.settingsSummaryData
}