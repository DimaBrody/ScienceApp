package com.brody.arxiv.shared.settings.general.domain.usecases

import com.brody.arxiv.shared.settings.general.domain.repository.SettingsRepository
import com.brody.arxiv.shared.settings.general.models.domain.QueryDataModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQueryPrefUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<QueryDataModel> =
        settingsRepository.queryData
}