package com.brody.arxiv.shared.settings.source

import com.brody.arxiv.shared.settings.models.domain.QueryDataModel
import kotlinx.coroutines.flow.Flow


interface SettingsDataSource {
    val queryData: Flow<QueryDataModel>

    suspend fun updateQuery(queryDataModel: QueryDataModel)
}