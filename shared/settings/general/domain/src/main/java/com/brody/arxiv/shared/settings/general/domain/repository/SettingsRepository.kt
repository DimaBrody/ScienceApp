package com.brody.arxiv.shared.settings.general.domain.repository

import com.brody.arxiv.shared.settings.general.models.domain.QueryDataModel
import com.brody.arxiv.shared.settings.general.models.domain.SettingsDataModel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val queryData: Flow<QueryDataModel>

    suspend fun updateQuery(queryDataModel: QueryDataModel)

    val settingsData: Flow<SettingsDataModel>

    suspend fun updateSettings(settingsDataModel: SettingsDataModel)
}