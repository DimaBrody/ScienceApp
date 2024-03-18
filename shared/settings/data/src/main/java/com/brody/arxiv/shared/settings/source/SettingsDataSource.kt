package com.brody.arxiv.shared.settings.source

import com.brody.arxiv.shared.settings.models.domain.QueryDataModel
import com.brody.arxiv.shared.settings.models.domain.SettingsDataModel
import kotlinx.coroutines.flow.Flow


interface SettingsDataSource {
    val queryData: Flow<QueryDataModel>

    val settingsData: Flow<SettingsDataModel>

    suspend fun updateQuery(queryDataModel: QueryDataModel)

    suspend fun updateSettings(settingsDataModel: SettingsDataModel)


}