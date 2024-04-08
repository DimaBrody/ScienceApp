package com.brody.arxiv.shared.settings.general.source

import com.brody.arxiv.shared.settings.general.models.domain.QueryDataModel
import com.brody.arxiv.shared.settings.general.models.domain.SettingsDataModel
import kotlinx.coroutines.flow.Flow


interface SettingsDataSource {
    val queryData: Flow<QueryDataModel>

    val settingsData: Flow<SettingsDataModel>

    suspend fun updateQuery(queryDataModel: QueryDataModel)

    suspend fun updateSettings(settingsDataModel: SettingsDataModel)


}