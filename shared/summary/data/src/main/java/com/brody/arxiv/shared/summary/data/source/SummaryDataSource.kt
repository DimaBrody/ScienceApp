package com.brody.arxiv.shared.summary.data.source

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.brody.arxiv.shared.summary.models.data.SummaryEntity
import com.brody.arxiv.shared.summary.models.domain.SettingsSummaryDataModel
import com.brody.arxiv.shared.summary.models.domain.SummaryLanguage
import com.brody.arxiv.shared.summary.models.domain.SummaryType
import kotlinx.coroutines.flow.Flow


interface SummaryDataSource {
    val settingsData: Flow<SettingsSummaryDataModel>

    suspend fun updateSaveSummaries(isSaveSummaries: Boolean)

    suspend fun updateSummaryType(summaryType: SummaryType)

    suspend fun updateSummaryLanguage(language: SummaryLanguage)

    fun getSummaryById(id: String): Flow<SummaryEntity?>

    suspend fun saveSummary(type: SummaryType, summary: String, id: String)
}