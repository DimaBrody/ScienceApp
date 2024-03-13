package com.brody.arxiv.shared.settings.domain.repository

import com.brody.arxiv.shared.settings.models.domain.QueryDataModel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val queryData: Flow<QueryDataModel>

    suspend fun updateQuery(queryDataModel: QueryDataModel)
}