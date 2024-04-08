package com.brody.arxiv.shared.settings.general.repository

import com.brody.arxiv.shared.settings.general.domain.repository.SettingsRepository
import com.brody.arxiv.shared.settings.general.models.domain.QueryDataModel
import com.brody.arxiv.shared.settings.general.models.domain.SettingsDataModel
import com.brody.arxiv.shared.settings.general.source.SettingsDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
) : SettingsRepository {

    override val queryData: Flow<QueryDataModel>
        get() = settingsDataSource.queryData

    override suspend fun updateQuery(queryDataModel: QueryDataModel) {
        settingsDataSource.updateQuery(queryDataModel)
    }

    override val settingsData: Flow<SettingsDataModel>
        get() = settingsDataSource.settingsData

    override suspend fun updateSettings(settingsDataModel: SettingsDataModel) {
        settingsDataSource.updateSettings(settingsDataModel)
    }


}